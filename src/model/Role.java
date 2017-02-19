package model;

/**
 * Created with IntelliJ IDEA.
 * User: h.hosseininejad
 * Date: 9/2/15
 * Time: 9:49 AM
 * To change this template use File | Settings | File Templates.
 */
public enum Role {
    UNIT_MANAGER, //modire vahed
    CUSTOMER_MANAGER, //modir moshtari
    SENIOR_EXPERT, //karshenas arshad
    EXPERT, //karshenas
    SECRETARY //masool daftar
    ;

    public static Role getRole(String persianName) {
        if (persianName.equals("مدیر واحد")) {
            return UNIT_MANAGER;
        } else if (persianName.equals("مدیر مشتری")) {
            return CUSTOMER_MANAGER;
        } else if (persianName.equals("کارشناس ارشد")) {
            return SENIOR_EXPERT;
        } else if (persianName.equals("کارشناس")) {
            return EXPERT;
        } else if (persianName.equals("مسئول دفتر")) {
            return SECRETARY;
        } else {
            return null;
        }
    }
}
