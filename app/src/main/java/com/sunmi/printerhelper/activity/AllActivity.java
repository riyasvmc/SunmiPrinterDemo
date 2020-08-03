package com.sunmi.printerhelper.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.sunmi.printerhelper.R;
import com.sunmi.printerhelper.utils.BluetoothUtil;
import com.sunmi.printerhelper.utils.BytesUtil;
import com.sunmi.printerhelper.utils.ESCUtil;
import com.sunmi.printerhelper.utils.SunmiPrintHelper;

import java.io.UnsupportedEncodingException;

/**
 *  Print all content by esc cmd
 *   @author kaltin
 */
public class AllActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all);
        setMyTitle(R.string.all_title);
        setBack();

        findViewById(R.id.multi_one).setOnClickListener(this);
        findViewById(R.id.multi_two).setOnClickListener(this);
        findViewById(R.id.multi_three).setOnClickListener(this);
        findViewById(R.id.multi_four).setOnClickListener(this);
        findViewById(R.id.multi_five).setOnClickListener(this);
        findViewById(R.id.multi_six).setOnClickListener(this);
    }

    /**
     *  Print all ESC commands supported by sunmi printer
     *  To verify that the printer works ok
     */
    public void onPrint(View view) {
        byte[] rv = BytesUtil.customData();
        Bitmap head = BitmapFactory.decodeResource(getResources(), R.drawable.sunmi);
        //Print raster bitmap (print raster bitmap ——normal)
        rv = BytesUtil.byteMerger(rv, ESCUtil.printBitmap(head, 0));
        //Print raster bitmap double width (print raster bitmap ——double width)
        rv = BytesUtil.byteMerger(rv,ESCUtil.printBitmap(head, 1));
        //Print raster bitmap ——double height
        rv = BytesUtil.byteMerger(rv,ESCUtil.printBitmap(head, 2));
        //Print raster bitmap double width and height (print raster bitmap ——double width and height)
        rv = BytesUtil.byteMerger(rv,ESCUtil.printBitmap(head, 3));
        //Select bitmap command 8 points single density (print Bitmap mode)
        rv = BytesUtil.byteMerger(rv,ESCUtil.selectBitmap(head, 0));
        //Select bitmap command 8 points double density
        rv = BytesUtil.byteMerger(rv,ESCUtil.selectBitmap(head, 1));
        //Select bitmap command 24 points single density
        rv = BytesUtil.byteMerger(rv,ESCUtil.selectBitmap(head, 32));
        //Select bitmap command 24 points double density
        rv = BytesUtil.byteMerger(rv,ESCUtil.selectBitmap(head, 33));
        //The displayable ascii code and tab will be output later
        rv = BytesUtil.byteMerger(rv,BytesUtil.wordData());
        byte[] ascii = new byte[96];
        for(int i = 0; i < 95; i++){
            ascii[i] = (byte) (0x20+i);
        }
        ascii[95] = 0x0A;
        rv = BytesUtil.byteMerger(rv, ascii);
        char[] tabs = new char[116];
        for(int i = 0; i < 116; i++){
            tabs[i] = (char) (0x2500 + i);
        }
        String test = new String(tabs);
        try {
            rv = BytesUtil.byteMerger(rv, test.getBytes("gb18030"));
            rv = BytesUtil.byteMerger(rv, new byte[]{0x0A});
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(BluetoothUtil.isBlueToothPrinter){
            BluetoothUtil.sendData(rv);
        }else{
            SunmiPrintHelper.getInstance().sendRawData(rv);
        }
    }

    @Override
    public void onClick(View v) {
        byte[] rv = null;
        switch (v.getId()){
            case R.id.multi_one:
                rv = BytesUtil.getBaiduTestBytes();
                break;
            case R.id.multi_two:
                rv = BytesUtil.getMeituanBill();
                break;
            case R.id.multi_three:
                rv = BytesUtil.getErlmoData();
                break;
            case R.id.multi_four:
                rv = BytesUtil.getKoubeiData();
                break;
            case R.id.multi_five:
                rv = ESCUtil.printBitmap(BytesUtil.initBlackBlock(384));
                break;
            case R.id.multi_six:
                rv = ESCUtil.printBitmap(BytesUtil.initBlackBlock(800,384));
                break;
            default:
                break;
        }
        if(BluetoothUtil.isBlueToothPrinter){
            BluetoothUtil.sendData(rv);
        }else{
            SunmiPrintHelper.getInstance().sendRawData(rv);
        }
    }
}
