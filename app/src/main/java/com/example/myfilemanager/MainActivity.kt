package com.example.myfilemanager

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.storage.StorageManager
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.widget.Toast
import java.lang.reflect.Array
import java.lang.reflect.InvocationTargetException

class MainActivity : AppCompatActivity() {
    companion object {
        const val T_DIR = 0// 文件夹
        const val T_FILE = 1// 文件
    }
    private final var REQUEST_WRITE_EXTERNAL_STORAGE=99
     lateinit var list: List<FileInfo>// 数据
     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //1.动态权限的获取。
        checkPermission()
         Log.e("sd_card", Environment.getExternalStorageDirectory().absolutePath)
         Log.e("sd_card", Environment.getExternalStorageDirectory().absolutePath)
        //2.获取文件列表
        list = FileManagerUtils.getListData(getStoragePath(this, false)!!)// 数据
         for (index in 0..list.size-1) {
            System.out.println("FileNmae="+list[index])
             Log.e("erro","FileNmae="+list[index]+"\n")
         }


     }



    /**
     * 反射调用获取内置存储和外置sd卡根路径
     * @param mContext    上下文
     * @param haveSdCard 是否有卡槽外置卡，false返回内部存储，true返回外置sd卡
     * @return
     */
    private fun getStoragePath(mContext: Context, haveSdCard: Boolean): String? {

        val mStorageManager = mContext.getSystemService(Context.STORAGE_SERVICE) as StorageManager
        var storageVolumeClazz: Class<*>? = null
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume")
            val getVolumeList = mStorageManager.javaClass.getMethod("getVolumeList")
            val getPath = storageVolumeClazz!!.getMethod("getPath")
            val isRemovable = storageVolumeClazz.getMethod("isRemovable")
            val result = getVolumeList.invoke(mStorageManager)
            val length = Array.getLength(result)
            for (i in 0 until length) {
                val storageVolumeElement = Array.get(result, i)
                val path = getPath.invoke(storageVolumeElement) as String
                val removable = isRemovable.invoke(storageVolumeElement) as Boolean
                Log.e("isRemove",removable.toString())

                if (haveSdCard == removable) {
                    return path
                }
            }
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

        return null
    }
    private fun checkPermission() {
        //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                            .WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show()
            }
            //申请权限
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_WRITE_EXTERNAL_STORAGE)

        } else {
            Toast.makeText(this, "授权成功！", Toast.LENGTH_SHORT).show()
        }
    }
}