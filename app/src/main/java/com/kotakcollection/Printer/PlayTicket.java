package com.kotakcollection.Printer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import com.kotakcollection.R;

import org.json.JSONObject;

import java.io.OutputStream;

public class PlayTicket {

    private String lock = "lockAccess";
    private MiddleWare JMI = new MiddleWare();
    final static byte[] underline_OFF = {0x1b, 0x2d, 0x0};
    final static byte[] underline_ONN_1 = {0x1b, 0x2d, 0x1};
    final static byte[] underline_ONN_2 = {0x1b, 0x2d, 0x2};
    final static byte[] lineSpace_DEFAULT = {0x1b, 0x32};
    final static byte[] lineSpace_VAL = {0x1b, 0x33, 0x23};
    final static byte[] Initialize = {0x1b, 0x40};
    final static byte[] emphasized_ON = {0x1b, 0x45, 0x1};
    final static byte[] emphasized_OFF = {0x1b, 0x45, 0x1};
    final static byte[] doublestrike_ON = {0x1b, 0x47, 0x1};
    final static byte[] doublestrike_OFF = {0x1b, 0x47, 0x1};
    final static byte[] FEED_LINES = {0x1b, 0x4A, 0x9};
    final static byte[] FONT_A = {0x1b, 0x4D, 0x0};
    final static byte[] FONT_B = {0x1b, 0x4D, 0x1};
    final static byte[] justification_LEFT = {0x1b, 0x61, 0x0};
    final static byte[] justification_CENTER = {0x1b, 0x61, 0x1};
    final static byte[] justification_RIGHT = {0x1b, 0x61, 0x2};
    final static byte[] character_SIZE_DOUBLE = {0x1d, 0x21, 0x10};
    final static byte[] character_SIZE_NORMAL = {0x1d, 0x21, 0x00};
    final static byte[] HRI_FONT_A = {0x1d, 0x66, 0x0};
    final static byte[] HRI_FONT_B = {0x1d, 0x66, 0x1};
    final static byte[] BARCODE_HEIGHT = {0x1d, 0x68, (byte) 162};
    final static byte[] BARCODE_WIDTH_2 = {0x1d, 0x77, 0x2};
    final static byte[] BARCODE_WIDTH_3 = {0x1d, 0x77, 0x3};

    //final static byte[] LOGO_1={0x1c,0x70,0x01,0x0};
    final static byte[] STATUS = {0x10, 0x04, 0x02};
    final static byte[] LAN_GERMAN = {0x1b, 0x23, 0x23, 0x53, 0x4C, 0x41, 0x4E, 0x02};

    public String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

    public String padLeft(String s, int n) {
        return String.format("%1$" + n + "s", s);
        //String.format("%10s", "foo");

    }

    private static final int BIT_WIDTH = 384;

    private static final int WIDTH = 48;

    private static final int GSV_HEAD = 8;

    public byte[] printBitmap(Bitmap bm, int bitMarginLeft, int bitMarginTop) {
        return printBitmap(bm, bitMarginLeft, bitMarginTop, true);
    }

    public byte[] printBitmap(Bitmap bm, int bitMarginLeft, int bitMarginTop,
                              boolean alreadyOpen) {

        return printBitmapGSVMSB(bm, bitMarginLeft, bitMarginTop, alreadyOpen);

    }

    private byte[] printBitmapGSVMSB(Bitmap bm, int bitMarginLeft, int bitMarginTop,
                                     boolean alreadyOpen) {
        byte[] result = generateBitmapArrayGSV_MSB(bm, bitMarginLeft, bitMarginTop);


        int lines = (result.length - GSV_HEAD) / WIDTH;
        System.arraycopy(new byte[]{
                0x1D, 0x76, 0x30, 0x00, 0x30, 0x00, (byte) (lines & 0xff),
                (byte) ((lines >> 8) & 0xff)
        }, 0, result, 0, GSV_HEAD);
        return result;

    }

    private byte[] generateBitmapArrayGSV_MSB(Bitmap bm, int bitMarginLeft, int bitMarginTop) {
        byte[] result = null;
        int n = bm.getHeight() + bitMarginTop;
        int offset = GSV_HEAD;
        result = new byte[n * WIDTH + offset];
        for (int y = 0; y < bm.getHeight(); y++) {
            for (int x = 0; x < bm.getWidth(); x++) {
                if (x + bitMarginLeft < BIT_WIDTH) {
                    int color = bm.getPixel(x, y);
                    int alpha = Color.alpha(color);
                    int red = Color.red(color);
                    int green = Color.green(color);
                    int blue = Color.blue(color);
                    if (alpha > 128 && (red < 128 || green < 128 || blue < 128)) {
                        // set the color black
                        int bitX = bitMarginLeft + x;
                        int byteX = bitX / 8;
                        int byteY = y + bitMarginTop;
                        result[offset + byteY * WIDTH + byteX] |= (0x80 >> (bitX - byteX * 8));
                    }
                } else {
                    // ignore the rest data of this line
                    break;
                }
            }
        }
        return result;
    }

    /* private Bitmap CreateCode(String str,com.google.zxing.BarcodeFormat type,int bmpWidth,int bmpHeight) throws WriterException {
         BitMatrix matrix = new MultiFormatWriter().encode(str, type,bmpWidth,bmpHeight);
         int width = matrix.getWidth();
         int height = matrix.getHeight();
         int[] pixels = new int[width * height];
         for (int y = 0; y < height; y++) {
             for (int x = 0; x < width; x++) {
                 if(matrix.get(x, y)){
                     pixels[y * width + x] = 0xff000000;
                 }else{
                     pixels[y * width + x] = 0xffffffff;
                 }
             }
         }
         Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
         bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
         return bitmap;
     }
    */
   /* private Bitmap myprintBarcode(String str)
    {
        try {
            //Bitmap bitmap = CreateCode(str, BarcodeFormat.UPC_A, 360, 108);
            //Bitmap bitmap = CreateCode(str, BarcodeFormat.UPC_A, 256, 70);
            //Bitmap bitmap = CreateCode(str, BarcodeFormat.UPC_A, 360, 80);
            //str="01234567890123456";
            Bitmap bitmap = CreateCode(str, BarcodeFormat.CODE_128, 384, 80);
            if (bitmap != null) {
                try
                {
                    Thread.sleep(100);
                }catch (Exception ignored){}
                return bitmap;
            }
        }catch (Exception ignored){}
        return null;
    }
    public int printTkt(Context cxt, String str, int reprint)
    {
        try{
            if (JMI.printerOpen(cxt) == false)
                return 0;
            String strPrnStatus=chkPrnStatus(cxt);
            if(!strPrnStatus.equalsIgnoreCase("OK"))
            {
                return 0;
            }
            synchronized (lock) {
                try {
                    //str="98403555552EF793A245~9840372214330253~09/08/17~12:01:42~528~09/08/17~03:30 PM~122~P2 AT N50|01;02;03;04;|~ ~300~24550.00~ ~401~0|";
                    int llen=15,rlen=15;
                    MiddleWare JMI=MiddleWare.GetInstance();
                    String strA[]=str.split("[~]");
                    String TransId=strA[1];
                    String Pur_date=strA[2];
                    String Pur_time=strA[3];
                    String strDrNo=strA[4];
                    String strDrDate=strA[5];
                    String strDrTime=strA[6];
                    String RefId=strA[7];
                    String strInfo=strA[8];
                    //String strMrp=strA[9];
                    String strTotStake=strA[10];
                    String strPromotion="";
                    //String PAID="";
                    UlVilliyatu ulVilliyatu=JMI.getGameDetails(RefId);
                    String strLotterName=ulVilliyatu.getVILAIYATU_PEYAR();
                    String strGameName=ulVilliyatu.getKUZHU_PEYAR();
                    try {
                        strPromotion=strA[12];
                        strLotterName+=strA[13];
                    }catch (Exception ignored){}
                    int len=TransId.length();
                    if(len>16)
                        TransId=TransId.substring(0,16);
                    String strValidity=ulVilliyatu.getVALIDITY();
                    //String strPromotion1=ulVilliyatu.getVILAMPARA_PEYAR();

                    OutputStream out = MiddleWare.mBTSocket.getOutputStream();
                    out.write(FONT_B);
                    out.write(justification_CENTER);
                    String strTextToPrint="TSN:"+TransId;
                    if(reprint==1)
                        strTextToPrint+="(R)\n";
                    else
                        strTextToPrint+="\n";
                    out.write(strTextToPrint.getBytes("utf-8"));
                    out.write(justification_LEFT);
                    //out.write(LOGO_1);
                    out.write(justification_CENTER);
                    strTextToPrint=strGameName+"\n";
                    out.write(character_SIZE_DOUBLE);
                    out.write(strTextToPrint.getBytes("Cp1252"));
                    out.write(character_SIZE_NORMAL);

                    out.write(FONT_A);
                    strTextToPrint=strLotterName+"\n";
                    out.write(strTextToPrint.getBytes("Cp1252"));

                    out.write(justification_LEFT);
                    strTextToPrint =padRight("Terminal Id",rlen);
                    String strTextToPrint2=padLeft(JMI.TERMINAL_ID,llen);
                    strTextToPrint=strTextToPrint+strTextToPrint2+"\n";
                    out.write(strTextToPrint.getBytes("Cp1252"));

                    strTextToPrint=padRight("Draw No",rlen);
                    strTextToPrint2=padLeft(strDrNo +" "+strDrDate,llen);
                    strTextToPrint=strTextToPrint+strTextToPrint2+"\n";
                    out.write(strTextToPrint.getBytes("Cp1252"));

                    strTextToPrint=padRight("Draw Time",rlen);
                    strTextToPrint2=padLeft(strDrTime,llen);
                    strTextToPrint=strTextToPrint+strTextToPrint2+"\n";
                    out.write(strTextToPrint.getBytes("Cp1252"));

                    strTextToPrint=padRight("Validity",rlen);
                    strTextToPrint2=padLeft(strValidity,llen);
                    strTextToPrint=strTextToPrint+strTextToPrint2+"\n";
                    out.write(strTextToPrint.getBytes("Cp1252"));

                    String sttLine="--------------------------------\n";
                    strTextToPrint=sttLine;
                    out.write(strTextToPrint.getBytes("Cp1252"));

                    String strB[]=strInfo.split("[|]");
                    //String strcur_symbol=getString(R.string.currency_symbol);
                    //String strcur="AT "+strcur_symbol;
                    String strcur_symbol="";
                    for(String Line:strB)
                    {
                        if(Line.length()>1)
                        {
                            {
                                strTextToPrint=Line.replace(';',' ');
                                strTextToPrint=strTextToPrint.replace("PHP","");
                                strTextToPrint+="\n";
                                out.write(strTextToPrint.getBytes("Cp1252"));
                            }
                        }
                    }
                    strTextToPrint=sttLine;
                    out.write(strTextToPrint.getBytes("Cp1252"));

                    strTextToPrint=padRight("Total ",rlen);
                    strTextToPrint2=padLeft(strcur_symbol+strTotStake,llen);
                    strTextToPrint=strTextToPrint+strTextToPrint2+"\n";
                    out.write(strTextToPrint.getBytes("Cp1252"));

                    strTextToPrint=sttLine;
                    out.write(strTextToPrint.getBytes("Cp1252"));

                    out.write(FONT_A);
                    strTextToPrint="TID :"+JMI.TERMINAL_ID;
                    strTextToPrint2=Pur_date+" "+Pur_time;
                    strTextToPrint=strTextToPrint+strTextToPrint2+"\n";
                    out.write(strTextToPrint.getBytes("Cp1252"));
                    try {
                        try {
                            Thread.sleep(500);
                        } catch (Exception ignored) {
                        }
                        Bitmap bmp = myprintBarcode(TransId);
                        if (bmp != null) {
                            out.write(printBitmap(bmp, 0, 0));
                            try {
                                Thread.sleep(500);
                            } catch (Exception ignored) {
                            }
                            strTextToPrint = "\n";
                            out.write(strTextToPrint.getBytes("Cp1252"));
                        }
                    }catch (Exception ignored){}
                    strTextToPrint="\n\n\n";
                    out.write(strTextToPrint.getBytes("Cp1252"));
                    *//*if(strPromotion1.length()>3)
                    {
                        strTextToPrint=strPromotion1;
                        strTextToPrint+="\n";
                        out.write(strTextToPrint.getBytes("Cp1252"));

                    }
                    if(strPromotion.length()>3)
                    {
                        strPromotion=strPromotion.replace("|","\n");
                        strTextToPrint=strPromotion+"\n";
                        out.write(strTextToPrint.getBytes("Cp1252"));
                    }
                    if(reprint==1)
                    {
                        Calendar cal=JMI.getVC_Calendar();
                        SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.US);
                        strTextToPrint = format1.format(cal.getTime());
                        strTextToPrint="Re-Print On:"+strTextToPrint;
                        out.write(strTextToPrint.getBytes("Cp1252"));
                    }
                    else if(reprint==2)
                    {
                        Calendar cal=JMI.getVC_Calendar();
                        SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.US);
                        strTextToPrint = format1.format(cal.getTime());
                        strTextToPrint="L-Print On:"+strTextToPrint;
                        out.write(strTextToPrint.getBytes("Cp1252"));
                    }
                    else if(reprint==3)
                    {
                        Calendar cal=JMI.getVC_Calendar();
                        SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.US);
                        strTextToPrint = format1.format(cal.getTime());
                        out.write(strTextToPrint.getBytes("Cp1252"));
                    }
                    strTextToPrint="\n\n\n\n\n\n";
                    out.write(strTextToPrint.getBytes("Cp1252"));*//*
                    try {
                        Thread.sleep(500);
                    }catch (Exception ignored){}
                    return 5;

                } catch (Exception ignored) {
                }
            }

        }catch (Exception ignored){}
        return 0;

    }
    private String chkPrnStatus(Context cxt)
    {
        int m=0;
        if(m==0)
            return "OK";
        String strRet=cxt.getString(R.string.prn_error)+": "+cxt.getString(R.string.prn_status0);;
        try{
            OutputStream out = MiddleWare.mBTSocket.getOutputStream();
            out.write(STATUS);
            InputStream in=MiddleWare.mBTSocket.getInputStream();
            int ret=in.read();
            if(ret==18)
                strRet="OK";
            else
            {
                Thread.sleep(1000);
                out = MiddleWare.mBTSocket.getOutputStream();
                out.write(STATUS);
                in=MiddleWare.mBTSocket.getInputStream();
                ret=in.read();
                if(ret==18)
                    strRet="OK";
                else
                    strRet=cxt.getString(R.string.prn_error)+": "+cxt.getString(R.string.prn_status8);
            }
        }catch(Exception ex){}
        //Log.d("PlayTicket", "chkPrnStatus: "+strRet);
        return strRet;
		*//*String strRet=cxt.getString(R.string.prn_error)+": "+cxt.getString(R.string.prn_status0);
		try{
			OutputStream out = MiddleWare.mBTSocket.getOutputStream();
    		out.write(STATUSNEW);
    		Thread.sleep(200);
    		byte[] buffer=new byte[500];
    		InputStream in=MiddleWare.mBTSocket.getInputStream();
    		int ret=in.read(buffer);
    		if(ret<4)
    		{
    			out.write(STATUSNEW);
    			Thread.sleep(500);
        		ret=in.read(buffer);
    		}
    		//int ret=in.read();
    		//if(ret==18)
    		if(ret>=4)
    		{
    			if(buffer[0]==20 && buffer[1]==0 && buffer[2]==12 && buffer[3]==15 )
    				strRet=cxt.getString(R.string.prn_error)+": "+cxt.getString(R.string.prn_status1)+" / "+cxt.getString(R.string.prn_status3);
    			else
    				strRet="OK";
    		}
			else
			{
				strRet=cxt.getString(R.string.prn_error)+": "+cxt.getString(R.string.prn_status8);
			}
		}catch(Exception ex){}
		//Log.d("PlayTicket", "chkPrnStatus: "+strRet);
		return strRet;*//*
    }*/

    public int test_Print(Context cxt) {
        try {
            if (JMI.printerOpen(cxt) == false)
                return 0;
            synchronized (lock) {
                OutputStream out = MiddleWare.mBTSocket.getOutputStream();
                out.write(justification_LEFT);
                //out.write(LOGO_1);
                out.write(justification_LEFT);
                out.write(FONT_A);
                out.write("Font A Test\n".getBytes("Cp1252"));
                out.write(FONT_B);
                out.write("Font B Test\n".getBytes("Cp1252"));
                out.write(doublestrike_ON);
                out.write("Bold 1 test\n".getBytes("Cp1252"));
                out.write(doublestrike_OFF);
                out.write(emphasized_ON);
                out.write("Bold 2 test\n".getBytes("Cp1252"));
                out.write(emphasized_OFF);
                out.write(justification_CENTER);
                out.write("CENTER justification\n".getBytes("Cp1252"));
                out.write(justification_RIGHT);
                out.write("RIGHT justification\n".getBytes("Cp1252"));
                out.write(justification_LEFT);
                out.write("LEFT justification\n".getBytes("Cp1252"));
                out.write(FONT_A);
                out.write(character_SIZE_DOUBLE);
                out.write("A MAX\n".getBytes("Cp1252"));
                out.write(character_SIZE_NORMAL);
                out.write("A normal\n".getBytes("Cp1252"));

                out.write(FONT_B);
                out.write(character_SIZE_DOUBLE);
                out.write("B MAX\n".getBytes("Cp1252"));
                out.write(character_SIZE_NORMAL);
                out.write("B normal\n".getBytes("Cp1252"));

                out.write("Test print complete\n\n\n\n".getBytes("Cp1252"));
            }
        } catch (Exception ex) {
        }
        return 0;
    }

    public int PrintReciept(Context cxt, JSONObject object) {


        try {
            Log.e(":", object.getString("ReceiptNo"));
            Log.e(":", object.getString("ReceiptDate"));
            Log.e(":", object.getString("PaymentType"));
            Log.e(":", object.getString("CollectedAmount"));
            Log.e(":", object.getString("CustomerName"));
            Log.e(":", object.getString("AccountNo"));
            Log.e(":", object.getString("CustomerMobileNo"));
            Log.e(":", object.getString("FEName"));

            if (JMI.printerOpen(cxt) == false)
                return 0;
            synchronized (lock) {

                OutputStream out = MiddleWare.mBTSocket.getOutputStream();
                Bitmap image = BitmapFactory.decodeResource(cxt.getResources(), R.drawable.logobmp);
                /*BitmapDrawable drawable = (BitmapDrawable)cxt.getResources()
                        .getDrawable(drawableId);
                bitmap = drawable.getBitmap();*/
                printBitmap(image, 10, 0);
                out.write(justification_LEFT);
                //out.write(LOGO_1);
                out.write(justification_LEFT);
                out.write(FONT_A);

                out.write(object.getString("ReceiptNo").getBytes("Cp1252"));

                out.write(object.getString("ReceiptDate").getBytes("Cp1252"));

                out.write(object.getString("PaymentType").getBytes("Cp1252"));

                out.write(object.getString("CollectedAmount").getBytes("Cp1252"));

                out.write(object.getString("CustomerName").getBytes("Cp1252"));

                out.write(object.getString("AccountNo").getBytes("Cp1252"));

                out.write(object.getString("CustomerMobileNo").getBytes("Cp1252"));

                out.write(object.getString("FEName").getBytes("Cp1252"));


               /* out.write("Font A Test\n".getBytes("Cp1252"));
                out.write(FONT_B);
                out.write("Font B Test\n".getBytes("Cp1252"));
                out.write(doublestrike_ON);
                out.write("Bold 1 test\n".getBytes("Cp1252"));
                out.write(doublestrike_OFF);
                out.write(emphasized_ON);
                out.write("Bold 2 test\n".getBytes("Cp1252"));
                out.write(emphasized_OFF);
                out.write(justification_CENTER);
                out.write("CENTER justification\n".getBytes("Cp1252"));
                out.write(justification_RIGHT);
                out.write("RIGHT justification\n".getBytes("Cp1252"));
                out.write(justification_LEFT);
                out.write("LEFT justification\n".getBytes("Cp1252"));
                out.write(FONT_A);
                out.write(character_SIZE_DOUBLE);
                out.write("A MAX\n".getBytes("Cp1252"));
                out.write(character_SIZE_NORMAL);
                out.write("A normal\n".getBytes("Cp1252"));

                out.write(FONT_B);
                out.write(character_SIZE_DOUBLE);
                out.write("B MAX\n".getBytes("Cp1252"));
                out.write(character_SIZE_NORMAL);
                out.write("B normal\n".getBytes("Cp1252"));*/

                out.write("--------------------------\n".getBytes("Cp1252"));
                out.write("cheque subject to realization\n\n\n\n".getBytes("Cp1252"));
            }
        } catch (Exception ex) {
        }
        return 0;
    }
    /*
    public int downloadLogo(Context cxt)
    {
        try{
            OutputStream out = MiddleWare.mBTSocket.getOutputStream();
            int ch;
            long m=0;
            AssetManager assManager = cxt.getAssets();
            InputStream f = null;
            f = assManager.open("pcsologo.tlg");
            while((ch=f.read())!=-1)
            {
                out.write(ch);
                m++;
                try
                {
                    if(m==300)
                    {
                        Thread.sleep(5);
                        m=0;
                    }
                }
                catch (Exception e) {}
            }
            f.close();
            //JMI.showMessage(cxt, cxt.getString(R.string.prn_logo_upload_s), cxt.getString(R.string.ok),0);
            return 1;
        }catch(Exception ex){
            //JMI.showMessage(cxt, cxt.getString(R.string.prn_logo_upload_f), cxt.getString(R.string.ok),1);
        }
        return 0;
    }
    public int printClaim(Context cxt,String strdata,int reprint)
    {
        try{
            //if(!bPrint)
            //  return 5;
            SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(cxt);
            boolean gametype=SP.getBoolean("checkboxPref", false);
            if(gametype)
            {
                String strA[]=strdata.split("[~]");
                String TransId=strA[1];
                String strClaimDate=strA[2];
                String strPrizecode=strA[3];
                String strStatuse=strA[4];
                String strPrizeAmnt=strA[5];
                JMI.CR_LIMIT=strA[6];
                String strPromotion=strA[7];

                OutputStream out = MiddleWare.mBTSocket.getOutputStream();
                out.write(FONT_B);
                out.write(justification_CENTER);
                String strTextToPrint="CONFIRMATION SLIP"+"\n";
                strTextToPrint+="   Congratulations!   "+"\n";
                strTextToPrint+="Terminal Id :"+JMI.TERMINAL_ID+"\n";
                strTextToPrint+="Date        :"+strClaimDate+"\n";
                strTextToPrint+="Prize Code  :"+strPrizecode+"\n";
                strTextToPrint+="TXN         :"+TransId+"\n";
                strTextToPrint+="Prize Amount:"+strPrizeAmnt+"\n";
                if(strStatuse.equals("2"))
                {
                    strTextToPrint+="* Payment in Camp Office *"+"\n";
                }
                if(strPromotion.length()>3)
                {
                    strPromotion+=strPromotion.replace("|","\n");
                    strTextToPrint+=strPromotion+"\n";out.write(strTextToPrint.getBytes("utf-8"));
                }
                strTextToPrint+="\n\n\n";
                JMI.showMessage(cxt,strTextToPrint,cxt.getString(R.string.ok),0,0);
                return 5;
            }
            String strPrnStatus=chkPrnStatus(cxt);
            MiddleWare JMI = MiddleWare.GetInstance();
            if(!strPrnStatus.equalsIgnoreCase("OK"))
            {
                //JMI.showMessage(cxt,strPrnStatus,cxt.getString(R.string.ok),0,0);
                return 0;
            }
            String strTextToPrint;
            synchronized (lock)
            {
                String strA[]=strdata.split("[~]");
                String TransId=strA[1];
                String strClaimDate=strA[2];
                String strPrizecode=strA[3];
                String strStatuse=strA[4];
                String strPrizeAmnt=strA[5];
                JMI.CR_LIMIT=strA[6];
                String strPromotion=strA[7];

                OutputStream out = MiddleWare.mBTSocket.getOutputStream();
                out.write(FONT_B);
                out.write(justification_CENTER);
                strTextToPrint="CONFIRMATION SLIP"+"\n";
                out.write(strTextToPrint.getBytes("utf-8"));
                strTextToPrint="   Congratulations!   "+"\n";
                out.write(strTextToPrint.getBytes("utf-8"));

                strTextToPrint="Terminal Id :"+JMI.TERMINAL_ID+"\n";
                out.write(strTextToPrint.getBytes("utf-8"));

                strTextToPrint="Date        :"+strClaimDate+"\n";
                out.write(strTextToPrint.getBytes("utf-8"));

                strTextToPrint="Prize Code  :"+strPrizecode+"\n";
                out.write(strTextToPrint.getBytes("utf-8"));

                strTextToPrint="TXN         :"+TransId+"\n";
                out.write(strTextToPrint.getBytes("utf-8"));

                strTextToPrint="Prize Amount:"+strPrizeAmnt+"\n";
                out.write(strTextToPrint.getBytes("utf-8"));

                if(strStatuse.equals("2"))
                {
                    strTextToPrint="* Payment in Camp Office *"+"\n";
                    out.write(strTextToPrint.getBytes("utf-8"));
                }
                if(strPromotion.length()>3)
                {
                    strPromotion=strPromotion.replace("|","\n");
                    strTextToPrint=strPromotion+"\n";out.write(strTextToPrint.getBytes("utf-8"));
                }
                if(reprint==1)
                {
                    Calendar cal=JMI.getVC_Calendar();
                    SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.US);
                    strTextToPrint = format1.format(cal.getTime());
                    strTextToPrint="Re-Print On "+strTextToPrint+"\n";
                    out.write(strTextToPrint.getBytes("utf-8"));
                }
                strTextToPrint="\n\n\n";
                out.write(strTextToPrint.getBytes("Cp1252"));

                return 5;

            }
        }catch(Exception ex){
            String Result = ex.toString();
            switch (Result) {
                case "com.telpo.tps550.api.printer.NoPaperException":
                    //strStatus="No PAPER";
                    return 0;
                case "com.telpo.tps550.api.printer.OverHeatException":
                    //strStatus="OVER HEAT";
                    return 0;
                default:
                    //strStatus="UNKNOWN";
                    return 0;
            }
        }
    }*/
}
