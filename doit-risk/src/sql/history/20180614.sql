update sysAutoReviewRule u set u.ruleData='Rupiahplus#Dana Cepat#Tangbull#Pinjam Uang#Vloan#UangTeman#Kredit Pintar#KTA Kilat#GoRupiah#Tunaiku#Tunaikita#creditku#easycash#Modalku Dana Usaha#masBro#Home Credit India#Ahli Pinjarman#doctor rupiah#Funding Societies#Taralite#Investree#Koinworks#Amartha#PundiPundi#DanaRupiah#D-Card#Vcard#Angel Cash#CashKilat#Dana Siaga#Zidisha#Danamas#RajaUang#masbro#Kredivo#mentimun#Kredit HP#Cicil#DanaBijak#Kredina#Dana Pintar#Kredit Usaha Rakyat#UangMe#Pinjaman Rp#MicroMoney#KartuOne#HALO RUPIAH#Easy Rupiah#BCA bank#eMoney#DBS bank#NISSAN FINANCE#MANDIRI UTAMA FINANCE#KreditPlus#CIMB Niaga#KAS WAGON INDONESIA#Mega Visa Gold#trx BDI CICIL#Kartu Cicilan#TCASH wallet#Rupiah Easy#Akulaku#Cashback#Sinar Mas Group#WeCash#Bank Central Asia#Pinjam Yuk#Kanpur#Loan Rp#MyRupiah#Rupiah cash#Cashwagon#Kartu Kredit ANZ'
where u.ruleDetailType in ('SMS_SAME_COUNT','MULTI_SMS_SAME_COUNT','MULTI_CONTACT_SAMEJOB_COUNT','CONTACT_SAME_PRODUCT_COUNT');



update sysAutoReviewRule u set u.ruleData='terlambat (.+) (\d+) hari#terlambat (\d+) hari#(\d+) hari (.+) tempo#(\d+) hari tempo#menunggak (.+) (\d+) hari#menunggak (\d+) hari#tempo (.+) (\d+) hari#tempo (\d+) hari#Keterlambatan (\d+) hari#Peringatan Terakhir tunggakan#Peringatan Terakhir (.*) tunggakan'
where u.ruleDetailType in ('SMS_OVERDUE_MORETHAN_15DAYS_COUNT','SMS_OVERDUE_LESSTHAN_15DAYS_COUNT','SMS_OVERDUE_MAX_DAYS','MULTI_SMS_OVERDUE_MORETHAN_15DAYS_COUNT','MULTI_SMS_OVERDUE_LESSTHAN_15DAYS_COUNT','MULTI_SMS_OVERDUE_MAX_DAYS');



update sysAutoReviewRule u set u.ruleData = 'terlambat bayar tagihan#tagihan menunggak#telah menunggak#lewat tempo#lewat (.*) tempo#telah tempo#telah (.*) tempo#sudah tempo#sudah (.*) tempo#sudah telambat#sudah (.*) telambat#sudah kadaluarsa#segera menyelesaikan tunggakan#segera menyelesaikan (.*) tunggakan'
where u.ruleDesc in ('MULTI_SMS_OVERDUE_COUNT','SMS_OVERDUE_COUNT');

commit;