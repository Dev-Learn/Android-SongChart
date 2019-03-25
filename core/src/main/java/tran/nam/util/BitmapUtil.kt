package tran.nam.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class BitmapUtil {

    companion object {

        private const val DEFAULT_WIDTH = 1500
        private const val DEFAULT_HEIGHT = 1500

        fun compressImageFile(context: Context, imageFile: File): File {
            return compressImageFile(context, imageFile, DEFAULT_WIDTH, DEFAULT_HEIGHT)
        }

        private fun compressImageFile(context: Context, imageFile: File, reqWidth: Int, reqHeight: Int): File {
            val tempDirectory = FileUtil.getDiskCacheDir(context, FileUtil.PROCESSING_DIR_NAME)
            if (!tempDirectory.exists())
                tempDirectory.mkdirs()
            val destinationPath = tempDirectory.path + File.separator + (System.currentTimeMillis().toString() + ".jpg")
            val destinationFile = File(destinationPath)
            if (destinationFile.exists()) {
                destinationFile.delete()
            }
            var fileOutputStream: FileOutputStream? = null

            try {
                fileOutputStream = FileOutputStream(destinationPath)
                decodeBitmapFromExitFile(imageFile, reqWidth, reqHeight).compress(
                    Bitmap.CompressFormat.JPEG,
                    50,
                    fileOutputStream
                )
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.flush()
                        fileOutputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }

            return File(destinationPath)
        }

        fun getBitmapFromCachedFile(file: File, bitmapConfig: Bitmap.Config = Bitmap.Config.RGB_565): Bitmap {
            val opts = BitmapFactory.Options()
            opts.inPreferredConfig = bitmapConfig
            return BitmapFactory.decodeFile(file.absolutePath, opts)
        }

        fun getBitmapFromFile(file: File, bitmapConfig: Bitmap.Config): Bitmap? {
            val opts = BitmapFactory.Options()
            opts.inPreferredConfig = bitmapConfig
            val bitmap: Bitmap? = BitmapFactory.decodeFile(file.absolutePath, opts) ?: return null
            return rotationBitmap(file.absolutePath, bitmap!!)
        }

        private fun rotationBitmap(filePath: String, bm: Bitmap): Bitmap {
            var exif: ExifInterface? = null
            try {
                exif = ExifInterface(filePath)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            var orientString: String? = null
            if (exif != null)
                orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION)

            val orientation =
                if (orientString != null) Integer.parseInt(orientString) else ExifInterface.ORIENTATION_NORMAL

            if (orientation != ExifInterface.ORIENTATION_NORMAL) {
                val rotationAngle = when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270
                    else -> 0
                }

                if (rotationAngle == 0)
                    return bm

                return rotationBitmap(bm, rotationAngle)
            }
            return bm
        }

        private fun rotationBitmap(bitmap: Bitmap, orientation: Int): Bitmap {
            val matrix = Matrix()
            matrix.setRotate(orientation.toFloat(), bitmap.width.toFloat() / 2, bitmap.height.toFloat() / 2)
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }

        fun decodeBitmapFromUri(
            context: Context, uri: Uri,
            reqWidth: Int = 0, reqHeight: Int = 0,
            bitmapConfig: Bitmap.Config = Bitmap.Config.RGB_565,
            crop: Boolean = false
        ): Bitmap? {
            val path: String?
            try {
                var bitmap: Bitmap
                path = FileUtil.getRealPathFromURI(context, uri)

                if (path != null && !path.isEmpty()) {
                    bitmap = decodeBitmapFromExitPath(path, reqWidth, reqHeight, bitmapConfig, crop)
                } else {
                    bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                    bitmap = scaleBitmap(bitmap, reqWidth, reqHeight)
                    if (crop && reqWidth > 0 && reqHeight > 0)
                        bitmap = crop(rotationBitmap(path!!, bitmap), reqWidth, reqHeight)
                }
                return rotationBitmap(path!!, bitmap)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            return null
        }

        fun decodeBitmapFromExitPath(
            path: String,
            reqWidth: Int = 0,
            reqHeight: Int = 0,
            bitmapConfig: Bitmap.Config = Bitmap.Config.RGB_565,
            crop: Boolean = false
        ): Bitmap {
            val opts = BitmapFactory.Options()
            opts.inJustDecodeBounds = true
            opts.inPreferredConfig = bitmapConfig
            BitmapFactory.decodeFile(path, opts)
            opts.inSampleSize = calculateInSampleSize(opts, reqWidth, reqHeight)
            opts.inJustDecodeBounds = false
            if (crop && reqWidth > 0 && reqHeight > 0)
                return crop(BitmapFactory.decodeFile(path, opts), reqWidth, reqHeight)
            return BitmapFactory.decodeFile(path, opts)
        }

        fun decodeBitmapFromExitFile(
            file: File,
            reqWidth: Int,
            reqHeight: Int,
            bitmapConfig: Bitmap.Config = Bitmap.Config.RGB_565,
            crop: Boolean = false
        ): Bitmap {
            return decodeBitmapFromExitPath(file.absolutePath, reqWidth, reqHeight, bitmapConfig, crop)
        }

        private fun crop(source: Bitmap, showWidth: Int, showHeight: Int): Bitmap {
            var bitmap = source
            val bitmapWidth = bitmap.width
            val bitmapHeight = bitmap.height

            val scaleWidth = showWidth / bitmapWidth.toFloat()
            val scaleHeight = showHeight / bitmapHeight.toFloat()

            val scale = if (scaleWidth > scaleHeight) scaleWidth else scaleHeight

            val destWidth = (bitmapWidth * scale).toInt()
            val destHeight = (bitmapHeight * scale).toInt()
            bitmap = Bitmap.createScaledBitmap(bitmap, destWidth, destHeight, false)
            if (scaleWidth < scaleHeight) {
                bitmap = Bitmap.createBitmap(bitmap, bitmap.width / 2 - showWidth / 2, 0, showWidth, destHeight)
            } else if (scaleWidth >= scaleHeight) {
                if (scaleWidth > scaleHeight)
                    bitmap = Bitmap.createBitmap(bitmap, 0, bitmap.height / 2 - showHeight / 2, destWidth, showHeight)
                else if (scaleWidth == scaleHeight)
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, destWidth, destHeight)
            }
            return bitmap
        }

        private fun scaleBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
            var widthTmp = bitmap.width
            var heightTmp = bitmap.height
            if (width != 0 && height != 0) {

                while (true) {
                    if (widthTmp < width || heightTmp < height)
                        break
                    widthTmp /= 2
                    heightTmp /= 2
                }
            }
            return Bitmap.createScaledBitmap(bitmap, widthTmp, heightTmp, false)
        }

        private fun calculateInSampleSize(option: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {

            val width = option.outWidth
            val height = option.outHeight

            if (height == 0 || width == 0) {
                return 1
            }

            val stretchWidth = Math.round(width.toFloat() / reqWidth.toFloat())
            val stretchHeight = Math.round(height.toFloat() / reqHeight.toFloat())

            return if (stretchWidth <= stretchHeight)
                stretchHeight
            else
                stretchWidth
        }

    }
}