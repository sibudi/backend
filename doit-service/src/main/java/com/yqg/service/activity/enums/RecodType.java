package com.yqg.service.activity.enums;

public enum RecodType {
    /**
     * 1，一级好友佣金 Komisi Teman Pertama
     2，二级好友佣金 Komisi Teman Kedua
     3，提现成功 Penarikan dana berhasil
     4，提现锁定中 Sedang menunggu penarikan dana
     5，提现失败 Penarikan dana gagal
     6, 提现失败退回 Pengembalian dana gagal
     7，我的邀请 Undangan saya
     8，佣金记录 Riwayat Komisi
     9，如何分享 Berbagi
     */

    Type1(1, "Komisi Teman Pertama"),
    Type2(2, "Komisi Teman Kedua"),
    Type3(3, "Penarikan dana berhasil"),
    Type4(4, "Sedang menunggu penarikan dana"),
    Type5(5, "Penarikan dana gagal"),
    Type6(6, "Pengembalian dana gagal");

    private Integer id;
    private String name;

    RecodType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static String getNameById(int id) {
        String name = "不支持的类型";
        for (RecodType statusEnum : RecodType.values()) {
            if (statusEnum.getId() == id) {
                name = statusEnum.getName();
            }
        }
        return name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
