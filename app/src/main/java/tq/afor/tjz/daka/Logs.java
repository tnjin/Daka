package tq.afor.tjz.daka;

import android.app.ExpandableListActivity;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Created by Tjz on 2016/10/6.
 */

public class Logs {

    private static final String LOG_FILE = Environment.getExternalStorageDirectory().getAbsolutePath()+"/daka.log";
    private static final String BLANK = "\t\t";
    public static void i(String tag,String msg){
        Log.i(tag,msg);
        PrintWriter out = null;
        try {
            out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File(LOG_FILE), true)));
            out.append(tag).append(BLANK).append(msg).append("\n");
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(out != null){
                out.flush();
                out.close();
            }
        }

    }



}
