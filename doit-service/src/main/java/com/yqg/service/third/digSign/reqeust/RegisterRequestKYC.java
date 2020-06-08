package com.yqg.service.third.digSign.reqeust;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestKYC {
    @JsonProperty(value = "userid")
    private String userId; // the doit account in digSign
    @JsonProperty(value = "alamat")
    private String idCardAddressDetail;  //Street name, House Number, Rt / Rw   (detail address in id card)
    @JsonProperty(value = "jenis_kelamin")
    private String gender;// male: laki-laki/ female: perempuan
    @JsonProperty(value = "kecamatan")
    private String smallDirect; // sub-district
    @JsonProperty(value = "kelurahan")
    private String bigDirect; // big district
    @JsonProperty(value = "kode-pos")
    private String postalCode; //邮政编码不校验但是必须传
    @JsonProperty(value = "kota")
    private String city;
    @JsonProperty(value = "nama")
    private String name;
    @JsonProperty(value = "tlp")
    private String mobileNumber;// mobile with country code eg 628.....
    @JsonProperty(value = "tgl_lahir")
    private String dateOfBirth;//dd-MM-yyyy (date of birth)

    @JsonProperty(value = "provinci")
    private String province;
    @JsonProperty(value = "idktp")
    private String nikKtp; //id card
    @JsonProperty(value = "tmp_lahir")
    private String placeOfBirth;
    @JsonProperty(value = "email")
    private String email;// user email
    @JsonProperty(value = "npwp")
    private String npwp = ""; //税卡号,不校验但是必须传
    @JsonProperty(value = "redirect")
    private Boolean redirect = false; //税卡号,不校验但是必须传
}
