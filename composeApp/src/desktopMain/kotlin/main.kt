import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.koin.core.context.startKoin

import whiskikiwhiskysearcher.composeapp.generated.resources.Res
import whiskikiwhiskysearcher.composeapp.generated.resources.app_name

@OptIn(ExperimentalResourceApi::class)
fun main() = application {
    startKoin {
        modules()
    }
    Window(onCloseRequest = ::exitApplication, title = stringResource(Res.string.app_name)) {
        App()
    }
}