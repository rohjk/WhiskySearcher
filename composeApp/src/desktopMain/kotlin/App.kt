import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seiko.imageloader.rememberImagePainter
import kotlinx.coroutines.launch
import model.Whisky
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import whiskikiwhiskysearcher.composeapp.generated.resources.Res
import whiskikiwhiskysearcher.composeapp.generated.resources.csv_export
import whiskikiwhiskysearcher.composeapp.generated.resources.delete
import whiskikiwhiskysearcher.composeapp.generated.resources.fetch_all_sort_by_newest
import whiskikiwhiskysearcher.composeapp.generated.resources.image_export
import whiskikiwhiskysearcher.composeapp.generated.resources.save
import whiskikiwhiskysearcher.composeapp.generated.resources.search
import whiskikiwhiskysearcher.composeapp.generated.resources.search_input_label
import whiskikiwhiskysearcher.composeapp.generated.resources.storage_dir_input_label

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    MaterialTheme {
        val searchRepository = remember { SearchRepository() }
        val csvGenerator = remember { CsvGenerator() }
        val imageDownloader = remember { ImageDownloader() }

        val scope = rememberCoroutineScope()

        var showLoading by remember { mutableStateOf(false) }
        var searchKeyword by remember { mutableStateOf("") }
        var storageDir by remember { mutableStateOf(getCurrentDate()) }

        var searchedWhiskys by remember { mutableStateOf(listOf<Whisky>()) }
        val checkedWhiskyMap: SnapshotStateMap<Long, Whisky> = remember { mutableStateMapOf() }

        var selectedIndex by remember { mutableStateOf(-1) }

        val searchResultScrollState = rememberLazyListState()
        val checkedWhiskyScrollState = rememberLazyListState()

        fun fetchAll() {
            showLoading = true
            scope.launch {
                selectedIndex = -1
                searchResultScrollState.scrollToItem(0)

                val result = searchRepository.fetchAll()
                searchedWhiskys = result
                showLoading = false
            }
        }

        fun search(keyword: String) {
            showLoading = true
            scope.launch {
                selectedIndex = -1
                searchResultScrollState.scrollToItem(0)

                val result = searchRepository.search(keyword)
                searchedWhiskys = result
                showLoading = false
            }
        }

        fun exportCsv() {
            csvGenerator.export(checkedWhiskyMap.values.toList(), storageDir)
        }

        fun exportImage() {
            showLoading = true
            scope.launch {
                imageDownloader.downloadImages(checkedWhiskyMap.values.toList())
                showLoading = false
            }
        }

        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(modifier = Modifier.size(25.dp)) {
                    if (showLoading) {
                        CircularProgressIndicator(modifier = Modifier.fillMaxSize())
                    }
                }

                Button(
                    modifier = Modifier.padding(horizontal = 5.dp),
                    onClick = { fetchAll() }
                ) {
                    Text(text = stringResource(Res.string.fetch_all_sort_by_newest))
                }

                TextField(
                    modifier = Modifier.weight(1f),
                    value = searchKeyword,
                    label = {
                        Text(text = stringResource(Res.string.search_input_label))
                    },
                    maxLines = 1,
                    onValueChange = { searchKeyword = it })

                Button(
                    modifier = Modifier.padding(start = 5.dp),
                    onClick = { search(searchKeyword) }
                ) {
                    Text(text = stringResource(Res.string.search))
                }

                TextField(
                    modifier = Modifier.width(180.dp).padding(start = 10.dp),
                    value = storageDir,
                    label = {
                        Text(
                            text = stringResource(Res.string.storage_dir_input_label),
                            fontSize = 10.sp
                        )
                    },
                    maxLines = 1,
                    onValueChange = { storageDir = it })

                Button(
                    modifier = Modifier.padding(horizontal = 5.dp),
                    onClick = { exportCsv() }
                ) {
                    Text(text = stringResource(Res.string.csv_export))
                }

                Button(
                    modifier = Modifier.padding(horizontal = 5.dp),
                    onClick = { exportImage() }
                ) {
                    Text(text = stringResource(Res.string.image_export))
                }
            }
            Divider(
                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                thickness = 3.dp,
                color = Color.DarkGray
            )

            Row(
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    state = searchResultScrollState,
                ) {
                    itemsIndexed(
                        items = searchedWhiskys,
                        key = { index, item -> "${item.id}_$index" }
                    ) { index: Int, item: Whisky ->
                        Text(
                            text = item.koName,
                            modifier = Modifier.fillMaxWidth()
                                .background(color = if (selectedIndex == index) Color.Gray else Color.Transparent)
                                .clickable {
                                    selectedIndex = if (selectedIndex == index) -1 else index
                                }
                        )
                        if (searchedWhiskys.lastIndex != index) {
                            Divider(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                                thickness = 1.dp,
                                color = Color.DarkGray
                            )
                        }
                    }
                }
                if (selectedIndex >= 0) {

                    val selectedItem = searchedWhiskys[selectedIndex]

                    var koName by remember(selectedItem) { mutableStateOf(selectedItem.koName) }
                    var enName by remember(selectedItem) { mutableStateOf(selectedItem.enName) }
                    var country by remember(selectedItem) { mutableStateOf(selectedItem.country) }
                    var category by remember(selectedItem) { mutableStateOf(selectedItem.category) }
                    var abv by remember(selectedItem) { mutableStateOf(selectedItem.abv) }

                    Column(
                        modifier = Modifier.width(500.dp).fillMaxHeight().verticalScroll(
                            rememberScrollState()
                        ),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val modifier = Modifier.fillMaxWidth()
                        val painter = rememberImagePainter(selectedItem.imageUrl)
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Image(
                                modifier = Modifier.size(250.dp).align(Alignment.Center),
                                painter = painter,
                                contentDescription = ""
                            )
                        }
                        TextField(
                            modifier = modifier,
                            value = koName,
                            onValueChange = { koName = it })
                        TextField(
                            modifier = modifier,
                            value = enName,
                            onValueChange = { enName = it })
                        TextField(
                            modifier = modifier,
                            value = country,
                            onValueChange = { country = it })
                        TextField(
                            modifier = modifier,
                            value = category,
                            onValueChange = { category = it })
                        TextField(modifier = modifier, value = abv, onValueChange = { abv = it })

                        Button(
                            modifier = modifier,
                            onClick = {
                                checkedWhiskyMap[selectedItem.id] = selectedItem.copy(
                                    koName = koName,
                                    enName = enName,
                                    country = country,
                                    category = category,
                                    abv = abv
                                )
                            }
                        ) {
                            Text(stringResource(Res.string.save))
                        }
                    }
                }

            }

            Divider(
                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                thickness = 3.dp,
                color = Color.DarkGray
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth().heightIn(min = 200.dp, max = 400.dp),
                state = checkedWhiskyScrollState,
            ) {
                itemsIndexed(
                    items = checkedWhiskyMap.values.toList().sortedBy { it.koName },
                    key = { index: Int, item: Whisky -> "${index}_${item.id}" }
                ) { index, item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            modifier = Modifier.padding(horizontal = 10.dp),
                            onClick = {
                                checkedWhiskyMap.remove(item.id)
                            }
                        ) {
                            Text(
                                fontSize = 12.sp,
                                text = stringResource(Res.string.delete)
                            )
                        }
                        Text(
                            text = "${index + 1}. " + item.toString(),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (index < checkedWhiskyMap.values.size -1 ) {
                        Divider(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                            thickness = 1.dp,
                            color = Color.DarkGray
                        )
                    }
                }
            }

        }
    }
}