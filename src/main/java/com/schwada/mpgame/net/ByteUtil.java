package com.schwada.mpgame.net;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ByteUtil {

    public static byte[] toByteArray(Object obj) throws IOException {
        byte[] bytes = null;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();
        } finally {
            if (oos != null) {
                oos.close();
            }
            if (bos != null) {
                bos.close();
            }
        }
        return bytes;
    }

    public static Object toObject(byte[] bytes)  {
        Object obj = null;
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        try {
            bis = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(bis);
            obj = ois.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(ByteUtil.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException ex) {
                    Logger.getLogger(ByteUtil.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException ex) {
                    Logger.getLogger(ByteUtil.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return obj;
    }

    public static String toString(byte[] bytes) {
        return new String(bytes);
    }

    public static void toObject(String hello) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
