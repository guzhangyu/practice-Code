package jvm;

import net.sf.cglib.proxy.MethodInterceptor;

/**
 * Created by guzy on 16/10/8.
 */
public class FinalizeEscapeGC {
    //MethodInterceptor

    public static FinalizeEscapeGC SAVE_HOOK=null;

    public void isAlive(){
        System.out.println("yes, i am still alive!");
    }

    @Override
    public void finalize() throws Throwable{
        super.finalize();
        System.out.println("finalize executed!");
        SAVE_HOOK=this;
    }

    public static void main(String[] args) throws InterruptedException {
        SAVE_HOOK=new FinalizeEscapeGC();

        SAVE_HOOK=null;
        System.gc();
        Thread.sleep(500);

        if(SAVE_HOOK!=null){
            SAVE_HOOK.isAlive();
        }else {
            System.out.println("no,i am dead!");
        }
        

        SAVE_HOOK=null;
        System.gc();
        Thread.sleep(500);

        if(SAVE_HOOK!=null){
            SAVE_HOOK.isAlive();
        }else {
            System.out.println("no,i am dead!");
        }

    }
}
