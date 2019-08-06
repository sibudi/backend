
-- 市插入
insert into sysDist(distName,distCode,distLevel,parentCode,language)
values(
    'Surakarta (Solo)',
    '616',
    '3',
    '7',
    'ID'
);

-- 大区插入
insert into sysDist(distName,distCode,distLevel,parentCode,language)
values('Pasar Kliwon', '8069','4','616','ID'),
      ('Jebres', '8070','4','616','ID'),
      ('Banjarsari', '8071','4','616','ID'),
      ('Laweyan', '8072','4','616','ID'),
      ('Serengan', '8073','4','616','ID');

-- 大区对应的小区
insert into sysDist(distName,distCode,distLevel,parentCode,language)
values('Kampung Baru', '92102', '5','8069','ID'),
      ('Kauman','92103','5','8069','ID'),
      ('Kedung Lumbu','92104','5','8069','ID'),
      ('Baluwarti','92105','5','8069','ID'),
      ('Gajahan','92106','5','8069','ID'),
      ('Joyosuran','92107','5','8069','ID'),
      ('Semanggi','92108','5','8069','ID'),
      ('Pasar Kliwon','92109','5','8069','ID'),
      ('Sangkrah','92110','5','8069','ID'),
     -- Jebres
      ('Sudiroprajan','92111','5','8070','ID'),
      ('Gandekan','92112','5','8070','ID'),
      ('Sewu','92113','5','8070','ID'),
      ('Jagalan','92114','5','8070','ID'),
      ('Pucang Sawit','92115','5','8070','ID'),
      ('Jebres','92116','5','8070','ID'),
      ('Mojosongo','92117','5','8070','ID'),
      ('Tegalharjo','92118','5','8070','ID'),
      ('Purwadiningratan','92119','5','8070','ID'),
      ('Kepatihan Wetan','92120','5','8070','ID'),
      ('Kepatihan Kulon','92121','5','8070','ID'),
  --  Banjarsari
      ('Timuran','92122','5','8071','ID'),
      ('Keprabon','92123','5','8071','ID'),
      ('Ketelan','92124','5','8071','ID'),
      ('Punggawan','92125','5','8071','ID'),
      ('Kestalan','92126','5','8071','ID'),
      ('Setabelan','92127','5','8071','ID'),
      ('Gilingan','92128','5','8071','ID'),
      ('Nusukan','92129','5','8071','ID'),
      ('Kadipiro','92130','5','8071','ID'),
      ('Banyuanyar','92131','5','8071','ID'),
      ('Sumber','92132','5','8071','ID'),
      ('Manahan','92133','5','8071','ID'),
      ('Mangkubumen','92134','5','8071','ID'),
  -- Laweyan
      ('Sriwedari','92135','5','8072','ID'),
      ('Penumping','92136','5','8072','ID'),
      ('Purwosari','92137','5','8072','ID'),
      ('Kerten','92138','5','8072','ID'),
      ('Jajar','92139','5','8072','ID'),
      ('Karangasem','92140','5','8072','ID'),
      ('Pajang','92141','5','8072','ID'),
      ('Sondakan','92142','5','8072','ID'),
      ('Laweyan','92143','5','8072','ID'),
      ('Bumi','92144','5','8072','ID'),
      ('Penularan','92145','5','8072','ID'),
  -- Serengan
      ('Kemlayan','92146','5','8073','ID'),
      ('Jayengan','92147','5','8073','ID'),
      ('Kratonan','92148','5','8073','ID'),
      ('Tipes','92149','5','8073','ID'),
      ('Serengan','92150','5','8073','ID'),
      ('Danukusuman','92151','5','8073','ID'),
      ('Joyotakan','92152','5','8073','ID')
;


update sysAutoReviewRule u set u.ruleData = concat(ruleData,'#Kabupaten Semarang#Kota Semarang#Kota Banjarmasin#Kota Bandar Lampung#Kota Pekanbaru#Kota Padang#Surakarta (Solo)')
where u.ruleDetailType in ('ORDER_ADDRESS_INVALID','LIVE_ADDRESS_INVALID','WORK_ADDRESS_INVALID','SCHOOL_ADDRESS_INVALID','ADDRESS_INVALID')
      and disabled=0;

update sysAutoReviewRule u set u.ruleData = concat(ruleData,'#024#0511#0721#0761#0751#0271#24#511#721#761#751#271')
where u.ruleDetailType ='COMPANY_TEL_NOT_IN_JAKARTA' and disabled=0;