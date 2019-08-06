package com.yqg.upload.utils;


public enum FilePath {
    A(1,"/A"),
    B(2,"/B"),
    C(3,"/C"),
    D(4,"/D"),
    E(5,"/E"),
    F(6,"/F"),
    G(7,"/G"),
    H(8,"/H"),
    I(9,"/I"),
    J(10,"/J"),
    K(11,"/K"),
    L(12,"/L"),
    M(13,"/M"),
    N(14,"/N"),
    O(15,"/O"),
    P(16,"/P"),
    Q(17,"/Q"),
    R(18,"/R"),
    S(19,"/S"),
    T(20,"/T"),
    U(21,"/U"),
    V(22,"/V"),
    W(23,"/W"),
    X(24,"/X"),
    Y(25,"/Y"),
    Z(26,"/Z");


    FilePath(int code,String path) {
        this.path = path;
        this.code = code;
    }

    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public static FilePath getCode(int code){
        for (FilePath e : FilePath.values()){
            if(code == e.getCode()) return e;
            continue;
        }
        return null;
    }
}
