package com.yqg.service.externalChannel.request;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

/*****
 * @Author tonggen
 * Created at 2018/12/27
 *
 ****/

@Getter
@Setter
public class CheetahBaseInfo {
    private CheetahBaseInfo.BankInfoBean bankInfo;
    private CheetahBaseInfo.DeviceInfoBean deviceInfo;
    private List<CheetahBaseInfo.EmergencyInfoBean> emergencyInfo;
    private CheetahBaseInfo.OrderInfoBean orderInfo;
    private CheetahBaseInfo.ResidentialInfoBean residentialInfo;
    private int uid;
    private CheetahBaseInfo.UserInfoBean userInfo;
    private CheetahBaseInfo.WorkInfoBean workInfo;

    @NoArgsConstructor
    @Data
    public static class BankInfoBean {
        /**
         * accountName : MICHAEL CIPUTRA
         * accountNumber : 1227819657
         * bankCode : PERMATA
         */

        private String accountName;
        private String accountNumber;
        private String bankCode;
    }

    @NoArgsConstructor
    @Data
    public static class DeviceInfoBean {

        private List<CheetahBaseInfo.DeviceInfoBean.ApplicationBean> application;
        private List<CheetahBaseInfo.DeviceInfoBean.AudioExternalBean> audio_external;
        private List<CheetahBaseInfo.DeviceInfoBean.AudioInternalBean> audio_internal;
        private CheetahBaseInfo.DeviceInfoBean.BatteryStatusBean battery_status;
        private CheetahBaseInfo.DeviceInfoBean.BlueToothBean blue_tooth;
        private List<CheetahBaseInfo.DeviceInfoBean.BrowserAndroidBean> browser_android;
        private List<CheetahBaseInfo.DeviceInfoBean.BrowserChromeBean> browser_chrome;
        private List<CheetahBaseInfo.DeviceInfoBean.CalendarAttendeesBean> calendar_attendees;
        private List<CheetahBaseInfo.DeviceInfoBean.CalendarEventsBean> calendar_events;
        private List<CheetahBaseInfo.DeviceInfoBean.CalendarsBean> calendars;
        private List<CheetahBaseInfo.DeviceInfoBean.CalenderRemindersBean> calender_reminders;
        private List<CheetahBaseInfo.DeviceInfoBean.CallBeanX> call;
        private List<CheetahBaseInfo.DeviceInfoBean.ConfiguredWifiBean> configured_wifi;
        private List<CheetahBaseInfo.DeviceInfoBean.ContactBean> contact;
        private List<CheetahBaseInfo.DeviceInfoBean.ContactAddressBean> contact_address;
        private List<CheetahBaseInfo.DeviceInfoBean.ContactEmailBean> contact_email;
        private List<CheetahBaseInfo.DeviceInfoBean.ContactGroupBean> contact_group;
        private List<CheetahBaseInfo.DeviceInfoBean.ContactPhoneBean> contact_phone;
        private CheetahBaseInfo.DeviceInfoBean.CurrentWifiBean current_wifi;
        private List<CheetahBaseInfo.DeviceInfoBean.DownloadFilesBean> download_files;
        private CheetahBaseInfo.DeviceInfoBean.GeneralDataBean general_data;
        private List<CheetahBaseInfo.DeviceInfoBean.GmailBean> gmail;
        private CheetahBaseInfo.DeviceInfoBean.HardwareBean hardware;
        private List<CheetahBaseInfo.DeviceInfoBean.ImagesExternalBean> images_external;
        private List<CheetahBaseInfo.DeviceInfoBean.ImagesInternalBean> images_internal;
        private CheetahBaseInfo.DeviceInfoBean.IpAddressBean ip_address;
        private CheetahBaseInfo.DeviceInfoBean.LocationBean location;
        private String network_env;
        private List<String> registered_accounts;
        private List<CheetahBaseInfo.DeviceInfoBean.SmsBean> sms;
        private List<CheetahBaseInfo.DeviceInfoBean.VideoExternalBean> video_external;
        private List<CheetahBaseInfo.DeviceInfoBean.VideoInternalBean> video_internal;
        private CheetahBaseInfo.DeviceInfoBean.VoiceBean voice;

        @NoArgsConstructor
        @Data
        public static class BatteryStatusBean {
            /**
             * battery_pct : 0.63
             * charge_info : {"is_ac_charge":true,"is_charging":true,"is_usb_charge":false}
             */

            private String battery_pct;
            private CheetahBaseInfo.DeviceInfoBean.BatteryStatusBean.ChargeInfoBean charge_info;

            @NoArgsConstructor
//            @Data
            public static class ChargeInfoBean {
                /**
                 * is_ac_charge : true
                 * is_charging : true
                 * is_usb_charge : false
                 */

                private boolean is_ac_charge;
                private boolean is_charging;
                private boolean is_usb_charge;

                public boolean isIs_ac_charge() {
                    return is_ac_charge;
                }

                public void setIs_ac_charge(boolean is_ac_charge) {
                    this.is_ac_charge = is_ac_charge;
                }

                public boolean isIs_charging() {
                    return is_charging;
                }

                public void setIs_charging(boolean is_charging) {
                    this.is_charging = is_charging;
                }

                public boolean isIs_usb_charge() {
                    return is_usb_charge;
                }

                public void setIs_usb_charge(boolean is_usb_charge) {
                    this.is_usb_charge = is_usb_charge;
                }
            }
        }

        @NoArgsConstructor
        @Data
        public static class BlueToothBean {
            /**
             * mac_address : 6C:5F:1C:2A:FE:F0
             * name : Coolpad A116
             * state : 10
             */

            private String mac_address;
            private String name;
            private int state;
        }

        @NoArgsConstructor
        @Data
        public static class CurrentWifiBean {
            /**
             * bssid : 0c:37:47:9b:85:25
             * mac_address : ec:1d:7f:73:98:f8
             * ssid : <unknown ssid>
             */

            private String bssid;
            private String mac_address;
            private String ssid;
        }

        @NoArgsConstructor
        @Data
        public static class GeneralDataBean {
            /**
             * telephony_configuration : {"keyboard":1,"locale_display_language":"Bahasa Indonesia","locale_iso3_country":"IDN","locale_iso3_language":"ind","mcc":510,"mnc":89,"time_zone_id":"Asia/Jakarta"}
             * telephony_info : {"android_id":"c23831eea8edc630","cid":24261796,"dbm":-31,"device_id":"866038027904534","imsi":"510897161158877","is_emulator":false,"is_rooted":true,"last_boot_time":1522754259854,"network_operator":"51089","network_operator_name":"3","phone_number":"","phone_type":1,"sim_country_iso":"id","sim_serial_number":"89628950001919178871"}
             */

            private CheetahBaseInfo.DeviceInfoBean.GeneralDataBean.TelephonyConfigurationBean telephony_configuration;
            private CheetahBaseInfo.DeviceInfoBean.GeneralDataBean.TelephonyInfoBean telephony_info;

            @NoArgsConstructor
            @Data
            public static class TelephonyConfigurationBean {
                /**
                 * keyboard : 1
                 * locale_display_language : Bahasa Indonesia
                 * locale_iso3_country : IDN
                 * locale_iso3_language : ind
                 * mcc : 510
                 * mnc : 89
                 * time_zone_id : Asia/Jakarta
                 */

                private int keyboard;
                private String locale_display_language;
                private String locale_iso3_country;
                private String locale_iso3_language;
                private int mcc;
                private int mnc;
                private String time_zone_id;
            }

            @NoArgsConstructor
//            @Data
            public static class TelephonyInfoBean {
                /**
                 * android_id : c23831eea8edc630
                 * cid : 24261796
                 * dbm : -31
                 * device_id : 866038027904534
                 * imsi : 510897161158877
                 * is_emulator : false
                 * is_rooted : true
                 * last_boot_time : 1522754259854
                 * network_operator : 51089
                 * network_operator_name : 3
                 * phone_number : 
                 * phone_type : 1
                 * sim_country_iso : id
                 * sim_serial_number : 89628950001919178871
                 */

                private String android_id;
                private int cid;
                private int dbm;
                private String device_id;
                private String imsi;
                private boolean is_emulator;
                private boolean is_rooted;
                private long last_boot_time;
                private String network_operator;
                private String network_operator_name;
                private String phone_number;
                private int phone_type;
                private String sim_country_iso;
                private String sim_serial_number;

                public String getAndroid_id() {
                    return android_id;
                }

                public void setAndroid_id(String android_id) {
                    this.android_id = android_id;
                }

                public int getCid() {
                    return cid;
                }

                public void setCid(int cid) {
                    this.cid = cid;
                }

                public int getDbm() {
                    return dbm;
                }

                public void setDbm(int dbm) {
                    this.dbm = dbm;
                }

                public String getDevice_id() {
                    return device_id;
                }

                public void setDevice_id(String device_id) {
                    this.device_id = device_id;
                }

                public String getImsi() {
                    return imsi;
                }

                public void setImsi(String imsi) {
                    this.imsi = imsi;
                }

                public boolean isIs_emulator() {
                    return is_emulator;
                }

                public void setIs_emulator(boolean is_emulator) {
                    this.is_emulator = is_emulator;
                }

                public boolean isIs_rooted() {
                    return is_rooted;
                }

                public void setIs_rooted(boolean is_rooted) {
                    this.is_rooted = is_rooted;
                }

                public long getLast_boot_time() {
                    return last_boot_time;
                }

                public void setLast_boot_time(long last_boot_time) {
                    this.last_boot_time = last_boot_time;
                }

                public String getNetwork_operator() {
                    return network_operator;
                }

                public void setNetwork_operator(String network_operator) {
                    this.network_operator = network_operator;
                }

                public String getNetwork_operator_name() {
                    return network_operator_name;
                }

                public void setNetwork_operator_name(String network_operator_name) {
                    this.network_operator_name = network_operator_name;
                }

                public String getPhone_number() {
                    return phone_number;
                }

                public void setPhone_number(String phone_number) {
                    this.phone_number = phone_number;
                }

                public int getPhone_type() {
                    return phone_type;
                }

                public void setPhone_type(int phone_type) {
                    this.phone_type = phone_type;
                }

                public String getSim_country_iso() {
                    return sim_country_iso;
                }

                public void setSim_country_iso(String sim_country_iso) {
                    this.sim_country_iso = sim_country_iso;
                }

                public String getSim_serial_number() {
                    return sim_serial_number;
                }

                public void setSim_serial_number(String sim_serial_number) {
                    this.sim_serial_number = sim_serial_number;
                }
            }
        }

        @NoArgsConstructor
        @Data
        public static class HardwareBean {
            /**
             * brand : OPPO
             * external_storage : /storage/sdcard1
             * main_storage : /storage/sdcard0
             * model : Stuart Hughes iPhone 6 Black Diamond
             * physical_size : 4.019027537991818
             * product : R831k
             * ram_total_size : 976.6953125
             * release : 6.1.0 (ngentot)
             * serial_number : 0123456789ABCDEF
             */

            private String brand;
            private String external_storage;
            private String main_storage;
            private String model;
            private String physical_size;
            private String product;
            private String ram_total_size;
            private String release;
            private String serial_number;
        }

        @NoArgsConstructor
        @Data
        public static class IpAddressBean {
            /**
             * ip_v4 : 10.45.141.192
             */

            private String ip_v4;
        }

        @NoArgsConstructor
        @Data
        public static class LocationBean {
            /**
             * gps : {"latitude":"-6.34462004","longitude":"106.89075578"}
             * network : {"latitude":"-5.5307359","longitude":"105.4490586"}
             */

            private CheetahBaseInfo.DeviceInfoBean.LocationBean.GpsBean gps;
            private CheetahBaseInfo.DeviceInfoBean.LocationBean.NetworkBean network;

            @NoArgsConstructor
            @Data
            public static class GpsBean {
                /**
                 * latitude : -6.34462004
                 * longitude : 106.89075578
                 */

                private String latitude;
                private String longitude;
            }

            @NoArgsConstructor
            @Data
            public static class NetworkBean {
                /**
                 * latitude : -5.5307359
                 * longitude : 105.4490586
                 */

                private String latitude;
                private String longitude;
            }
        }

        @NoArgsConstructor
        @Data
        public static class VoiceBean {
            /**
             * alert : {"current":4,"max":7}
             * call : {"current":2,"max":6}
             * music : {"current":4,"max":13}
             * notification : {"current":2,"max":7}
             * ring : {"current":2,"max":7}
             * system : {"current":2,"max":7}
             */

            private CheetahBaseInfo.DeviceInfoBean.VoiceBean.AlertBean alert;
            private CheetahBaseInfo.DeviceInfoBean.VoiceBean.CallBean call;
            private CheetahBaseInfo.DeviceInfoBean.VoiceBean.MusicBean music;
            private CheetahBaseInfo.DeviceInfoBean.VoiceBean.NotificationBean notification;
            private CheetahBaseInfo.DeviceInfoBean.VoiceBean.RingBean ring;
            private CheetahBaseInfo.DeviceInfoBean.VoiceBean.SystemBean system;

            @NoArgsConstructor
            @Data
            public static class AlertBean {
                /**
                 * current : 4
                 * max : 7
                 */

                private int current;
                private int max;
            }

            @NoArgsConstructor
            @Data
            public static class CallBean {
                /**
                 * current : 2
                 * max : 6
                 */

                private int current;
                private int max;
            }

            @NoArgsConstructor
            @Data
            public static class MusicBean {
                /**
                 * current : 4
                 * max : 13
                 */

                private int current;
                private int max;
            }

            @NoArgsConstructor
            @Data
            public static class NotificationBean {
                /**
                 * current : 2
                 * max : 7
                 */

                private int current;
                private int max;
            }

            @NoArgsConstructor
            @Data
            public static class RingBean {
                /**
                 * current : 2
                 * max : 7
                 */

                private int current;
                private int max;
            }

            @NoArgsConstructor
            @Data
            public static class SystemBean {
                /**
                 * current : 2
                 * max : 7
                 */

                private int current;
                private int max;
            }
        }

        @NoArgsConstructor
        @Data
        public static class ApplicationBean {
            /**
             * first_install_time : 1524699795000
             * flags : 13123141
             * last_update_time : 1524699795000
             * package_name : com.sonymobile.whitebalance
             * version_code : 4194305
             * version_name : 2.0.A.0.1
             */

            private String first_install_time;
            private String flags;
            private String last_update_time;
            private String package_name;
            private String version_code;
            private String version_name;
        }

        @NoArgsConstructor
        @Data
        public static class AudioExternalBean {
            /**
             * date_added : 1532435530
             * date_modified : 1230739200
             * duration : 24022
             * is_alarm : 0
             * is_music : 0
             * is_notification : 0
             * is_ringtone : 1
             * mime_type : application/ogg
             * year : 0
             */

            private String date_added;
            private String date_modified;
            private String duration;
            private String is_alarm;
            private String is_music;
            private String is_notification;
            private String is_ringtone;
            private String mime_type;
            private String year;
        }

        @NoArgsConstructor
        @Data
        public static class AudioInternalBean {
            /**
             * date_added : 1532435530
             * date_modified : 1230739200
             * duration : 24022
             * is_alarm : 0
             * is_music : 0
             * is_notification : 0
             * is_ringtone : 1
             * mime_type : application/ogg
             * year : 0
             */

            private String date_added;
            private String date_modified;
            private String duration;
            private String is_alarm;
            private String is_music;
            private String is_notification;
            private String is_ringtone;
            private String mime_type;
            private String year;
        }

        @NoArgsConstructor
        @Data
        public static class BrowserAndroidBean {
            /**
             * bookmark : 0
             * date : 1527488988147
             * title : ...
             * url : http://74.125.24.113/generate_204
             * visits : 1
             */

            private String bookmark;
            private String date;
            private String title;
            private String url;
            private String visits;
        }

        @NoArgsConstructor
        @Data
        public static class BrowserChromeBean {
            /**
             * bookmark : 0
             * date : 1532448005045
             * title : 5 Pertanyaan 
             * url : https://magazine.job-like.com/5-pertanyaan-yang/
             * visits : 2
             */

            private String bookmark;
            private String date;
            private String title;
            private String url;
            private String visits;
        }

        @NoArgsConstructor
        @Data
        public static class CalendarAttendeesBean {
            /**
             * attendee_relationship : 2
             * attendee_status : 1
             * attendee_type : 1
             * event_id : 96
             */

            private String attendee_relationship;
            private String attendee_status;
            private String attendee_type;
            private String event_id;
        }

        @NoArgsConstructor
        @Data
        public static class CalendarEventsBean {
            /**
             * _id : 1
             * access_level : 3
             * all_day : 1
             * availability : 0
             * calendar_id : 4
             * dtend : 1508630400000
             * dtstart : 1508544000000
             * event_status : 1
             * event_timezone : UTC
             * guests_can_invite_others : 1
             * guests_can_modify : 0
             * guests_can_see_guests : 1
             * has_alarm : 0
             * has_attendee_data : 1
             * has_extended_properties : 0
             * last_date : 1508630400000
             */

            private String _id;
            private String access_level;
            private String all_day;
            private String availability;
            private String calendar_id;
            private String dtend;
            private String dtstart;
            private String event_status;
            private String event_timezone;
            private String guests_can_invite_others;
            private String guests_can_modify;
            private String guests_can_see_guests;
            private String has_alarm;
            private String has_attendee_data;
            private String has_extended_properties;
            private String last_date;
        }

        @NoArgsConstructor
        @Data
        public static class CalendarsBean {
            /**
             * _id : 9
             * allowed_attendee_types : 0,1,2
             * allowed_availability : 0,1
             * allowed_reminders : 0,1,2
             * calendar_access_level : 200
             * calendar_timezone : Asia/Jakarta
             * visible : 0
             */

            private String _id;
            private String allowed_attendee_types;
            private String allowed_availability;
            private String allowed_reminders;
            private String calendar_access_level;
            private String calendar_timezone;
            private String visible;
        }

        @NoArgsConstructor
        @Data
        public static class CalenderRemindersBean {
            /**
             * event_id : 40
             * method : 1
             * minutes : 4320
             */

            private String event_id;
            private String method;
            private String minutes;
        }

        @NoArgsConstructor
        @Data
        public static class CallBeanX {
            /**
             * date : 1536156675900
             * duration : 45
             * is_read : 1
             * name : 
             * number : +6285384831876
             * type : 3
             */

            private String date;
            private String duration;
            private String is_read;
            private String name;
            private String number;
            private String type;
        }

        @NoArgsConstructor
        @Data
        public static class ConfiguredWifiBean {
            /**
             * bssid : Modal dong
             * mac_address : 
             * ssid : Modal dong
             */

            private String bssid;
            private String mac_address;
            private String ssid;

        }

        @NoArgsConstructor
        @Data
        public static class ContactBean {
            /**
             * _id : 1
             * contact_status : 
             * contact_status_ts : 
             * custom_ringtone : 
             * display_name : Bapak
             * has_phone_number : 1
             * in_visible_group : 1
             * is_user_profile : 0
             * last_time_contacted : 0
             * photo_id : 
             * send_to_voicemail : 0
             * starred : 0
             * times_contacted : 0
             */

            private String _id;
            private String contact_status;
            private String contact_status_ts;
            private String custom_ringtone;
            private String display_name;
            private String has_phone_number;
            private String in_visible_group;
            private String is_user_profile;
            private String last_time_contacted;
            private String photo_id;
            private String send_to_voicemail;
            private String starred;
            private String times_contacted;
        }

        @NoArgsConstructor
        @Data
        public static class ContactAddressBean {
            /**
             * contact_id : 277
             * data1 : Meruya Selatan
             * data10 : 
             * data7 : 
             * data8 : 
             * display_name : Dede Saepudin Grbe
             * starred : 0
             */

            private String contact_id;
            private String data1;
            private String data10;
            private String data7;
            private String data8;
            private String display_name;
            private String starred;
        }

        @NoArgsConstructor
        @Data
        public static class ContactEmailBean {
            /**
             * contact_id : 108
             * data1 : constantine.nathaniel@gmail.com
             */

            private String contact_id;
            private String data1;
        }

        @NoArgsConstructor
        @Data
        public static class ContactGroupBean {
            /**
             * _id : 1
             * account_name : Phone contacts
             * account_type : com.sonyericsson.localcontacts
             * auto_add : 0
             * deleted : 0
             * favorites : 0
             * group_is_read_only : 1
             * notes : 
             * should_sync : 1
             * title : Colleagues
             */

            private String _id;
            private String account_name;
            private String account_type;
            private String auto_add;
            private String deleted;
            private String favorites;
            private String group_is_read_only;
            private String notes;
            private String should_sync;
            private String title;
        }

        @NoArgsConstructor
        @Data
        public static class ContactPhoneBean {
            /**
             * contact_id : 28
             * data1 : +62818671257
             * display_name : Bi Ati
             */

            private String contact_id;
            private String data1;
            private String display_name;
        }

        @NoArgsConstructor
        @Data
        public static class DownloadFilesBean {
            /**
             * file_name : accs_election
             * file_type : 
             * last_modified : 1533331694000
             * length : 75
             */

            private String file_name;
            private String file_type;
            private String last_modified;
            private String length;
        }

        @NoArgsConstructor
        @Data
        public static class GmailBean {
            /**
             * _id : 0
             * name : Utama
             * num_conversations : 14
             * num_unread_conversations : 0
             */

            private String _id;
            private String name;
            private String num_conversations;
            private String num_unread_conversations;
        }

        @NoArgsConstructor
        @Data
        public static class ImagesExternalBean {
            /**
             * _size : 297959
             * date_added : 1532441031
             * date_modified : 1532441031
             * datetaken : 1485805169000
             * height : 960
             * latitude : -5.379427909851074
             * longitude : 105.25381469726562
             * mime_type : image/jpeg
             * title : IMG_1056
             * width : 1280
             */

            private String _size;
            private String date_added;
            private String date_modified;
            private String datetaken;
            private String height;
            private String latitude;
            private String longitude;
            private String mime_type;
            private String title;
            private String width;
        }

        @NoArgsConstructor
        @Data
        public static class ImagesInternalBean {
            /**
             * _size : 496881
             * date_added : 1532435524
             * date_modified : 1230739200
             * datetaken : 1230739200000
             * height : 1920
             * latitude : 0.0
             * longitude : 0.0
             * mime_type : image/jpeg
             * title : lockscreen_01
             * width : 1080
             */

            private String _size;
            private String date_added;
            private String date_modified;
            private String datetaken;
            private String height;
            private String latitude;
            private String longitude;
            private String mime_type;
            private String title;
            private String width;
        }

        @NoArgsConstructor
        @Data
        public static class SmsBean {
            /**
             * address : +62
             * body : Kode verifikasi Anda adalah 983595. Berlaku selama 10 menit. Gunakan kode ini untuk melakukan verifikasi akun anda sekarang
             * date : 1525896662980
             * date_sent : 0
             * read : 0
             * seen : 0
             * status : -1
             * type : 1
             */

            private String address;
            private String body;
            private String date;
            private String date_sent;
            private String read;
            private String seen;
            private String status;
            private String type;
        }

        @NoArgsConstructor
        @Data
        public static class VideoExternalBean {
            /**
             * _size : 11924818
             * date_added : 1534003075
             * date_modified : 1533965608
             * datetaken : 1533965608000
             * description : 
             * duration : 59281
             * isprivate : 0
             * language : 
             * latitude : 
             * longitude : 0.0
             * mime_type : video/mp4
             * resolution : 640x640
             * tags : 
             * title : 1minute_crafts__180811_123002
             */

            private String _size;
            private String date_added;
            private String date_modified;
            private String datetaken;
            private String description;
            private String duration;
            private String isprivate;
            private String language;
            private String latitude;
            private String longitude;
            private String mime_type;
            private String resolution;
            private String tags;
            private String title;
        }

        @NoArgsConstructor
        @Data
        public static class VideoInternalBean {
            /**
             * _size : 11924818
             * date_added : 1534003075
             * date_modified : 1533965608
             * datetaken : 1533965608000
             * description : 
             * duration : 59281
             * isprivate : 0
             * language : 
             * latitude : 
             * longitude : 0.0
             * mime_type : video/mp4
             * resolution : 640x640
             * tags : 
             * title : 1minute_crafts__180811_123002
             */

            private String _size;
            private String date_added;
            private String date_modified;
            private String datetaken;
            private String description;
            private String duration;
            private String isprivate;
            private String language;
            private String latitude;
            private String longitude;
            private String mime_type;
            private String resolution;
            private String tags;
            private String title;
        }
    }

    @NoArgsConstructor
    @Data
    public static class OrderInfoBean {
        /**
         * id : 252533517261475840
         * productId : 60
         */

        private String id;
        private int productId;
    }

    @NoArgsConstructor
    @Data
    public static class ResidentialInfoBean {
        /**
         * address : ji.marsha
         * city : Kota jakarta selatan
         * district : Pesanggrahan
         * homePhoneNumber : 18513102162
         * province : Dki jakarta
         * status : 1
         * staySince : 2018-08-18
         * village : Other
         * zipCode : 87137763
         */

        private String address;
        private String city;
        private String district;
        private String homePhoneNumber;
        private String province;
        private int status;
        private String staySince;
        private String village;
        private String zipCode;
    }

    @NoArgsConstructor
    @Data
    public static class UserInfoBean {
        /**
         * birthPlace : ji.marsha
         * birthday : 1983-12-07
         * education : 4
         * email : hello@gmail.com
         * fullName : MICHAEL CIPUTRA
         * gender : 1
         * handHeldPhoto : /MyUpload//U/V/c0dafb34005323dbbe0969a3a9b2ee5a.jpg
         * idExpiryDate : 2018-08-18
         * idFrontPhoto : /MyUpload//U/V/c0dafb34005323dbbe0969a3a9b2ee5a.jpg
         * idNumber : 3271015712830010
         * jobType : 2
         * ktpType : 1
         * loanPurpose : 5
         * maritalStatus : 1
         * mobile : 82378575521
         * monthlyIncome : 4000000
         * motherMaidenName : yi
         * npwp : 
         * numberOfDependents : 2
         * religion : 1
         * reserveMobile : 
         */

        private String birthPlace;
        private String birthday;
        private int education;
        private String email;
        private String fullName;
        private int gender;
        private String handHeldPhoto;
        private String idExpiryDate;
        private String idFrontPhoto;
        private String idNumber;
        private int jobType;
        private int ktpType;
        private int loanPurpose;
        private int maritalStatus;
        private String mobile;
        private int monthlyIncome;
        private String motherMaidenName;
        private String npwp;
        private int numberOfDependents;
        private int religion;
        private String reserveMobile;

        @Getter
        public enum ReligionEnum {
            //1=MOSLEM
//2=CHRISTIAN
//3=CATHOLIC
//4=HINDU
//5=BUDDHA
//6=CONFUSIUS
//7=OTHERS
            MOSLEM(1,"Islam"),
            CHRISTIAN(2,"Kristen Protestan"),
            CATHOLIC(3,"Kristen Katolik"),
            HINDU(4,"Hindu"),
            BUDDHA(5,"Buddha"),
            CONFUSIUS(6,"Konghucu"),
            OTHERS(7,"OTHERS");

            private Integer code;
            private String name;

            ReligionEnum(Integer code, String name) {
                this.code = code;
                this.name = name;
            }
            public static CheetahBaseInfo.UserInfoBean.ReligionEnum getByCode(int code) {
                return Arrays.asList(CheetahBaseInfo.UserInfoBean.ReligionEnum.values()).stream()
                        .filter(elem -> elem.getCode() == code).findFirst().get();
            }

        }

        @Getter
        public enum MaritalStatusEnum {
            //            1= Single  //2= Married //3= Divorced //4= Widowed //5= Others

            Single(1,"Single"),
            Married(2,"Married"),
            Divorced(3,"Divorced"),
            Widowed(4,"Widowed"),
            Others(5,"Others");

            private Integer code;
            private String name;

            MaritalStatusEnum(Integer code, String name) {
                this.code = code;
                this.name = name;
            }
            public static CheetahBaseInfo.UserInfoBean.MaritalStatusEnum getByCode(int code) {
                return Arrays.asList(CheetahBaseInfo.UserInfoBean.MaritalStatusEnum.values()).stream()
                        .filter(elem -> elem.getCode() == code).findFirst().get();
            }
        }


        @Getter
        public enum LoanPurposeEnum {
            //1=Livelihood 2=Consumptive buying 3=Working capital 4=Health / hospitalisation 5=Education 6=Holiday /
            // travel 7=Birth expenses 8=Wedding 9=House renovation 10=Others
            LIVELIHOOD(1,"Lainnya"),//其它
            CONSUMPTIVE_BUYING(2,"Lainnya"),//其它
            WORKING_CAPITAL(3,"Lainnya"),//其它
            HEALTH_OR_HOSPITALISATION(4,"Pengobatan"),//健康医疗
            EDUCATION(5,"Pendidikan"),//教育
            HOLIDAY_TRAVEL(6,"Liburan"),//旅游
            BIRTH_EXPENSES(7,"Lainnya"),
            WEDDING(8,"Pernikahan"),//婚庆
            HOUSE_RENOVATION(9,"HRenovasi"),//装修
            OTHERS(10,"Lainnya");//其它
            private Integer code;
            private String name;

            LoanPurposeEnum(Integer code, String name) {
                this.code = code;
                this.name = name;
            }

            public static CheetahBaseInfo.UserInfoBean.LoanPurposeEnum getByCode(int code) {
                return Arrays.asList(CheetahBaseInfo.UserInfoBean.LoanPurposeEnum.values()).stream()
                        .filter(elem -> elem.getCode() == code).findFirst().get();
            }
        }
    }

    @NoArgsConstructor
    @Data
    public static class WorkInfoBean {
        /**
         * businessAddress : chian
         * businessAddressCity : city
         * businessAddressDistrict : district
         * businessAddressProvince : 省
         * businessAddressVillage : village
         * companyPhone : 02122561717
         * employerName : pt
         * jobPosition : 1
         * occupationIndustry : 1
         * workSince : 2018-08-18
         */

        private String businessAddress;
        private String businessAddressCity;
        private String businessAddressDistrict;
        private String businessAddressProvince;
        private String businessAddressVillage;
        private String companyPhone;
        private String employerName;
        private int jobPosition;
        private int occupationIndustry;
        private String workSince;

        @Getter
        public enum OccupationIndustryEnum {
            //1=Agriculture 2=Building 3=Communication 4=Electricity, gas, and water 5=Finance 6=Government service 7=Indonesia national police 8=Lawyer Firm 9=Media, Film 10=Military 11=Mining and excavation
//12=Oil and gas
//13=Private services
//14=Trade, hotel, and restaurant
//15=Transportation
//16=Others
            AGRICULTURE(1,"Petani"),//农业
            BUILDING(2,"Pekerja"),//工人
            COMMUNICATION(3,"Lainnya"),
            ELECTRICITY(4,"Lainnya"),
            FINANCE(5,"Lainnya"),

            GOVERNMENT(6,"Pegawai negeri"),//公务员
            INDONESIA(7,"Keamanan"),//保安
            LAWYER(8,"Lainnya"),
            MEDIA(9,"Lainnya"),
            MILITARY(10,"Lainnya"),

            MINING(11,"Lainnya"),
            OIL(12,"Lainnya"),
            PRIVATE(13,"Pembantu rumah tangga"),//保姆
            TRADE(14,"Pelayan"),//服务员
            TRANSPORTATION(15,"Lainnya"),
            OTHERS(16,"Lainnya")
            ;


            private Integer code;
            private String name;

            OccupationIndustryEnum(Integer code, String name) {
                this.code = code;
                this.name = name;
            }
            public static CheetahBaseInfo.WorkInfoBean.OccupationIndustryEnum getByCode(int code) {

                return Arrays.asList(CheetahBaseInfo.WorkInfoBean.OccupationIndustryEnum.values()).stream().filter(elem -> elem.getCode() == code)
                        .findFirst().get();

            }
        }
    }

    @NoArgsConstructor
    @Data
    public static class EmergencyInfoBean {
        /**
         * mobile : 316966126
         * name : 百川燃气1
         * relation : 2
         */

        private String mobile;
        private String name;
        private int relation;
    }
}
