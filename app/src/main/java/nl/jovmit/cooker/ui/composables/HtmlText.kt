package nl.jovmit.cooker.ui.composables

import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.view.Gravity
import android.widget.TextView
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat

@Composable
fun HtmlText(
    modifier: Modifier = Modifier,
    html: String,
    textStyle: TextStyle = LocalTextStyle.current,
    maxLines: Int = Int.MAX_VALUE
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            val gravity = when(textStyle.textAlign) {
                TextAlign.Center -> Gravity.CENTER
                TextAlign.End -> Gravity.END
                else -> Gravity.START
            }
            TextView(context).apply {
                textSize = textStyle.fontSize.value
                setGravity(gravity)
                setMaxLines(maxLines)
                setTextColor(textStyle.color.toArgb())
                ellipsize = TextUtils.TruncateAt.END
                movementMethod = LinkMovementMethod.getInstance()
            }
        },
        update = {
            it.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT)
        }
    )
}