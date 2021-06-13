import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap

fun toMutableBitmap(bitmap: Bitmap):Bitmap?{
    return resizeImage( bitmap.copy(Bitmap.Config.ARGB_8888, true),100,100)
}


fun resizeImage(image: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap? {
    var resizedImage: Bitmap? = null
    try {
        var imageHeight = image.height
        if (imageHeight > maxHeight) imageHeight = maxHeight
        var imageWidth = (imageHeight * image.width
                / image.height)
        if (imageWidth > maxWidth) {
            imageWidth = maxWidth
            imageHeight = (imageWidth * image.height
                    / image.width)
        }
        if (imageHeight > maxHeight) imageHeight = maxHeight
        if (imageWidth > maxWidth) imageWidth = maxWidth
        resizedImage = Bitmap.createScaledBitmap(
            image, imageWidth,
            imageHeight, true
        )
    } catch (e: OutOfMemoryError) {
        e.printStackTrace()
    } catch (e: NullPointerException) {
        e.printStackTrace()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return resizedImage
}

inline fun <T> alertDialogShow(context: Context, crossinline onPositiveClick:()->T):AlertDialog{
    return AlertDialog.Builder(context)
        .setMessage("Are you sure you want to clear your drawing?")
        .setTitle("Warning")
        .setPositiveButton("OK"){dialog,which->
            onPositiveClick()
        }
        .setNegativeButton("NO"){dialog,_ ->
            dialog.dismiss()
        }
        .create()
}