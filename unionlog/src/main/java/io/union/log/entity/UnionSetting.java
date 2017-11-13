package io.union.log.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by Administrator on 2017/7/10.
 */
@Entity
@Table(name = "union_setting")
public class UnionSetting implements Serializable {

    @Id
    @Column(name = "setkey", unique = true)
    private String setkey;

    @Column(name = "setvalue")
    private String setvalue;

    public UnionSetting() {
    }

    public String getSetkey() {
        return setkey;
    }

    public void setSetkey(String setkey) {
        this.setkey = setkey;
    }

    public String getSetvalue() {
        return setvalue;
    }

    public void setSetvalue(String setvalue) {
        this.setvalue = setvalue;
    }
}
