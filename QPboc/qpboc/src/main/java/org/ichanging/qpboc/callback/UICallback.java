package org.ichanging.qpboc.callback;

/**
 * Created by ChangingP on 16/6/16.
 */
public interface UICallback {

    /**
     * User Response Some Data
     *
     * @param t    Support boolean int byte string ...
     * @param args More Response Data T
     * @param <T>
     */
    //    <T> void onSuccess(T t, Object... args)
    //    {
    //        for (Object var : args) {
    //            type = var.getClass().getSimpleName();
    //
    //            System.out.println(type);
    //
    //            if (var instanceof String) {
    //                System.out.println("String Value - " + (String) var);
    //            }
    //
    //            if (var instanceof byte[]) {
    //                System.out.println("Byte Value - " + var);
    //            }
    //
    //            if (var instanceof Integer) {
    //                System.out.println("Integer Value - " + (Integer) var);
    //            }
    //            if (var instanceof Boolean) {
    //                System.out.println("Boolean Value - " + (Boolean) var);
    //            }
    //        }
    //    }

    /**
     * User Cancel Operation
     *
     */
    void onCancel();

    /**
     * User Operate Timeout
     *
     */
    void onTimeout();

}
