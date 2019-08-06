package com.yqg.user.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@Data
@Table("usrLinkManInfo")
public class UsrLinkManInfo extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -8576900933451257658L;

    private String userUuid;
    private String contactsName;
    private String relation;
    private String contactsMobile;
    private Integer sequence;
    private String waOrLine;
    private String formatMobile;


    @Getter
    public enum SequenceEnum {
        FIRST(1),
        SECOND(2),
        OWNER_BACKUP(3),
        THIRD(4),
        FOURTH(5);

        SequenceEnum(int code) {
            this.code = code;
        }

        private int code;
    }

    @Getter
    public enum RelationEnum{
        PARENT(1, "Orang tua"),
        SPOUSE(2, "Pasangan"),
        BROTHER_OR_SISTER(3, "Saudara / saudari"),
        RELATIVE(4, "Keluarga dekat"),
        FRIEND(5, "Teman"),
        COLLEAGUE(6, "Rekan kerja"),
        FATHER(7,"Ayah"), //父亲
        MOTHER(8,"Ibu");//母亲

        RelationEnum(int code, String name) {
            this.code = code;
            this.name = name;
        }

        private int code;
        private String name;
    }
}