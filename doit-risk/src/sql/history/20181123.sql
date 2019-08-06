
-- 放开规则
update sysAutoReviewRule set specifiedProduct =1 where ruleDetailType in ('WORK_ADDRESS_INVALID','COMB_SAMEIPCOUNT_MALE',
'NIGHT_CALL_RATE','RECENT30_EVENING_CALL_RATE_MALE')
and disabled=0;

-- 新增规则
insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleData,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',2,
  'WORK_ADDRESS_INVALID_100RMB','工作地址不属于雅加达大区和爪哇岛和巴厘岛-100RMB','','Kabupaten Kepulauan Seribu#Kota Jakarta Barat#Kota Jakarta Pusat#Kota Jakarta
  Selatan#Kota Jakarta Timur#Kota Jakarta Utara#Kabupaten Tangerang#Kota Tangerang Selatan#Kota Tangerang#Kota Depok#Kota Bandung#Kabupaten Bandung#Kabupaten Bandung Barat#Kota Bogor#Kabupaten Bogor#Kota Bekasi#Kabupaten Bekasi#Kabupaten Karawang#Kota Cimahi#Kota Cirebon#Kabupaten Cirebon#Kabupaten Sumedang#Kota Tasikmalaya#Kabupaten Tasikmalaya#Kota Sukabumi#Kabupaten Sukabumi#Kabupaten Subang#Kabupaten Purwakarta#Kabupaten Indramayu#Kota Banjar#Kabupaten Badung#Kabupaten Bangli#Kabupaten Buleleng#Kabupaten Gianyar#Kabupaten Jembrana#Kabupaten Karangasem#Kabupaten Klungkung#Kabupaten Tabanan#Kota Denpasar#Kabupaten Serang#Kota Serang#Kota Yogyakarta#Kota Surabaya#Kota Pontianak#Kota Makassar#Kota Palembang#Kota Medan#Kabupaten Semarang#Kota Semarang#Kota Banjarmasin#Kota Bandar Lampung#Kota Pekanbaru#Kota Padang#Surakarta (Solo)#Kabupaten Sidoarjo#Kota Malang#Kabupaten Malang#Kota Batam#Kota Manado#Kabupaten Bantul#Kabupaten Kediri#Kota Kediri#Kabupaten Cianjur#Kabupaten Sleman#Kota Balikpapan#Kota Samarinda#Kabupaten Gresik#Kota Jambi#Kota Magelang#Kabupaten Magelang#Kabupaten Banyuwangi#Kabupaten Wonogiri#Kabupaten Banyumas#Kabupaten Garut#Kabupaten Jember',2,1,15,1890,"V1",3);

update sysAutoReviewRule set ruleData = concat(ruleData,'#Kota Surakarta#Kabupaten Deli Serdang#Kabupaten Sukoharjo#Kota Cilegon#Kabupaten
Pasuruan#Kabupaten Ciamis#Kabupaten Majalengka#Kabupaten Kuningan#Kabupaten Cilacap#Kabupaten Lebak#Kabupaten Mojokerto#Kabupaten Karanganyar#Kota
Mataram#Kabupaten Jombang#Kota Madiun#Kabupaten Klaten#KOTA TEGAL#Kabupaten Pandeglang#Kabupaten Kudus#Kabupaten Tulungagung#Kota
Bengkulu#Kabupaten Boyolali#Kabupaten Purbalingga#Kota Pematang Siantar#Kabupaten Lamongan#Kabupaten Banjar#Kabupaten Pati#Kabupaten
Bojonegoro#Kabupaten Tegal#Kota Banda Aceh#Kota Banjarbaru#Kabupaten Lampung Utara#Kabupaten Brebes#Kota Pekalongan#Kota Batu')
where
ruleDetailType =
'WORK_ADDRESS_INVALID_100RMB';


insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
  'COMB_SAMEIPCOUNT_MALE_100RMB','同一天内同一个IP的申请次数&男-100RMB','3',2,1,15,1895,"V1",3);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
  'NIGHT_CALL_RATE_100RMB','近30天内夜间活跃占比-100RMB','0.16',2,1,15,1900,"V1",3);

insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,
  ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo)
  values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',15,
  'RECENT30_EVENING_CALL_RATE_MALE_100RMB','近30天内夜间活跃占比&男-100RMB','0.1',2,1,15,1905,"V1",3);

