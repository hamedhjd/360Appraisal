package model;

/**
 * Created with IntelliJ IDEA.
 * User: h.hosseininejad
 * Date: 9/2/15
 * Time: 10:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class Question {
    private Integer code;
    private String text;

    public Question(Integer code, String text) {
        this.code = code;
        this.text = text;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
