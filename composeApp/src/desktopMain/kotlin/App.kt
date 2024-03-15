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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.rememberImagePainter
import kotlinx.coroutines.launch
import model.Whisky
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import whiskikiwhiskysearcher.composeapp.generated.resources.Res
import whiskikiwhiskysearcher.composeapp.generated.resources.search
import whiskikiwhiskysearcher.composeapp.generated.resources.search_input_label

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    MaterialTheme {
        val searchRepository = remember { SearchRepository() }
        val scope = rememberCoroutineScope()

        var showLoading by remember { mutableStateOf(false) }
        var searchKeyword by remember { mutableStateOf("") }
        var searchedWhisky by remember { mutableStateOf(listOf<Whisky>()) }

        var selectedIndex by remember { mutableStateOf(-1) }

        fun search(keyword: String) {
            showLoading = true
            scope.launch {
                val result = searchRepository.search(keyword)
                searchedWhisky = result
                showLoading = false
            }
        }

        val scrollState = rememberLazyListState()

        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
            Row(
                modifier = Modifier.widthIn(max = 500.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(25.dp)) {
                    if (showLoading) {
                        CircularProgressIndicator(modifier = Modifier.fillMaxSize())
                    }
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
                    onClick = { search(searchKeyword) }) {
                    Text(text = stringResource(Res.string.search))
                }
            }
            Divider(
                modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                thickness = 1.dp,
                color = Color.DarkGray
            )

            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    state = scrollState
                ) {
                    itemsIndexed(
                        items = searchedWhisky,
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
                    }
                }
                if (selectedIndex >= 0) {

                    val selectedItem = searchedWhisky[selectedIndex]

                    Column(
                        modifier = Modifier.width(500.dp).fillMaxHeight().verticalScroll(
                            rememberScrollState()
                        ),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val painter = rememberImagePainter(selectedItem.imageUrl)
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Image(
                                modifier = Modifier.size(250.dp).align(Alignment.Center),
                                painter = painter,
                                contentDescription = ""
                            )
                        }
                        Text(text = selectedItem.koName)
                        Text(text = selectedItem.enName)
                        Text(text = selectedItem.country)
                        Text(text = selectedItem.category)
                        Text(text = selectedItem.abv)
                    }
                }

            }
        }
    }
}