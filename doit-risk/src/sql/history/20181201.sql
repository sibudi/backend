
insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'男性&age<30',15,
  'MALE_AGE','男性年龄不符','30',2,1,15,1930,"V1",35,1);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'男性&学历为高中',15,
  'MALE_EDUCATION','性别&学历不符','Sekolah Menengah Atas',2,1,15,1935,"V1",35,1);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'男性&未提交税卡',15,
  'MALE_NOTAXNUMBER','性别&未提交税卡','',2,1,15,1940,"V1",35,1);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',2,
  'WORK_ADDRESS_NOT_VALID_NORMAL','工作城市不佳','Kota Jakarta Selatan#Kota Jakarta Pusat#Kota Jakarta Barat#Kota Surabaya#Kota Jakarta Utara#Kota
  Jakarta Timur#Kota Tangerang Selatan#Kota Tangerang#Kabupaten Bekasi#Kota Bekasi#Kota Bekasi#Kabupaten Bogor#Kabupaten Tangerang#Kota
  Depok#Kabupaten Sidoarjo#Kabupaten Bandung#Kota Malang#Kota Bogor#Kabupaten Karawang#Kota Denpasar#Kabupaten Badung#Kota Pekanbaru#Kota
  Batam#Kabupaten Malang#Kota Tasikmalaya#Kabupaten Cirebon#Kabupaten Purwakarta#Kota Sukabumi#Kabupaten Bandung Barat#Kota Banjarmasin#Kota
  Yogyakarta#Kota Kediri#Kabupaten Sleman#Kabupaten Bantul#Kabupaten Tasikmalaya#Surakarta#Solo#Kota Cilegon#Kabupaten Sukoharjo#Kabupaten
  Gianyar#Kabupaten Jembrana#Kabupaten Garut#Kota Samarinda#Kabupaten Lampung Selatan#Kabupaten Pangandaran#KABUPATEN BANYUWANGI#Kabupaten
  Jombang#Kabupaten Karanganyar#Kabupaten Minahasa Tenggara#Kota Blitar#Kabupaten Bulungan#Kabupaten Nganjuk#Kota Tomohon#Kabupaten Tanah
  Bumbu#Kabupaten Kebumen#Kabupaten Pekalongan#Kabupaten Tapanuli Utara#Kota Pematang Siantar#Kabupaten Bangkalan#Kabupaten Empat Lawang#Kabupaten
  Kotawaringin Barat#Kabupaten Sarolangun#Kota Prabumulih#Kabupaten Bangli#Kabupaten Labuhanbatu#Kabupaten Lahat#Kabupaten Jember#Kabupaten Maros',2,
  1,15,
  51,"V1",35,1);


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',25,
  'TOTAL_MEMORY_COMPANY_CALL_BANK_CODE_MALE','手机总内存&外呼公司电话返回结果&特定银行&男','2',2,1,15,1940,"V1",3,1);



  update sysAutoReviewRule s set s.appliedTo=34 where s.ruleDetailType = 'WORK_ADDRESS_INVALID' and disabled=0;
