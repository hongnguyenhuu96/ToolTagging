/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Sentence is type of (content, status, category)
 * (done###<act>mua</act> <obj>máy giặt</obj> <pty>sam sung</pty> <num>7.5 kg</num>::Công Nghệ)
 * @author Hong
 */
public class Sentence {
    String content;
    String status;

    public Sentence(String content , String status) {
        this.content = content;
        this.status = status;
    }
    
    public Sentence(){
        content = "";
        status = "";
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
