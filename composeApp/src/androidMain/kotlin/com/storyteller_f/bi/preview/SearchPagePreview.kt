package com.storyteller_f.bi.preview

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.storyteller_f.bi.components.CustomSearchBar
import com.storyteller_f.bi.ui.theme.BiTheme

@Composable
@Preview
private fun PreviewSearchPage() {
    BiTheme {
        Surface(modifier = Modifier.fillMaxWidth()) {
            CustomSearchBar(modifier = Modifier)
        }
    }
}
