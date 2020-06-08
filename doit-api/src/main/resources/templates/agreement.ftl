<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>Digisign</title>
    <style type="text/css">
        /*解决html转pdf文件中文不显示的问题*/
        body {
            font-family: SimSun;
        }

        /*设定纸张大小*/
        /* A4纸 */

        .base_page {
            width: 100%;
            height: 930px;
            /*background-color: #ccc;*/
            position: relative;
            border:.5px solid #000;
            margin-top: 20px;
            font-size: 13px;
        }

        .base_page_other {
            width: 100%;
            height: 1000px;
            /*background-color: #ccc;*/
            position: relative;
            border:.5px solid #000;
            margin-top: 20px;
            font-size: 13px;
        }

        .base_page_other2 {
            width: 100%;
            height: 975px;
            /*background-color: #ccc;*/
            position: relative;
            border:.5px solid #000;
            margin-top: 20px;
            font-size: 13px;
        }

        .qwe {
            width: 100%;
            height: 60px;
            text-align: center;
            font-size: 13px;
            line-height: 30px;
        }
        .base_pagetwo {
            width: 100%;
            height: 350px;
            /*background-color: #ccc;*/
            position: relative;
            border:.5px solid #000;
            margin-top: 20px;
            font-size: 13px;
        }
        .base_pageone {
            width: 100%;
            height: 400px;
            /*background-color: #ccc;*/
            position: relative;
            border:.5px solid #000;
            /*border-top: none;*/
            margin-top: 20px;
            font-size: 13px;
        }
        .base_page_empty {
            width: 100%;
            height: 250px;
            position: relative;
        }
        .con_two {
            font-size: 13px;
            margin-left: 50px;
            margin-top: 20px;
            /*width: 100%;*/
        }
        .t_qwe {
            height: 20px;
            font-size: 13px;
            margin-left: 15px;
            margin-top: 20px;
            width: 100%;
        }
        .twotit {
            height: 40px;
            font-size: 15px;
            line-height: 30px;
            text-align: center;

        }
        .qianmi {
            margin-top: 30px;
            height: 20px;
            line-height: 20px;
            width: 100%;
        }
        .qianmi1 {
            margin-top: 140px;
            height: 20px;
            line-height: 20px;
            width: 100%;
        }
        .left_page {
            width: 50%;
            height: 100%;
            border-right: .5px solid #000000;
            /*background-color: #ccc;*/
            float: left;
            overflow: hidden;
        }
        .rig_page {
            width: 49%;
            height: 100%;
            float: left;
        }
        .top_tit {
            width: 100%;
            height: 30px;
            /*line-height: 30px;*/
            text-align: left;
            font-weight: 600;
        }
        .left_cotain {
            width: 100%;
            height: 880px;
            line-height: 20px;

        }
        p {
            margin-left: 15px;
            margin-right: 15px;

        }
        .npsp {

            margin-left: 30px;
            margin-right: 15px;
        }
        .perjan {
            width: 100%;
            height: 70px;
            text-align: center;
            line-height: 30px;
            font-weight: bold;
        }
    </style>
</head>
<body>
<div class="perjan">
    PERJANJIAN PINJAM - MEMINJAM <br>
    LOAN AGREEMENT <br>
    No. ${orderNo}
</div>
<div class="base_page">
    <div class="left_page">
        <div class="left_cotain">
            <p>Perjanjian ini dibuat pada hari ${signDayId}, tanggal ${signDateID}, oleh dan antara:</p>
            <p><span style="font-weight: 700;">1. ${kuasaName} (bertindak sebagai Kuasa dari ${lenderName} sebagai Pemberi Pinjaman)</span>, yang berdomisili di ${kuasaDom}, dan pemegang Kartu Tanda Penduduk (KTP) No. ${kuasaIDCard} (untuk selanjutnya disebut sebagai “PIHAK PERTAMA”).</p>
            <p>2. ${realName}, perorangan dan Warga Negara Indonesia, yang berdomisili di ${liveCity}, dan pemegang Kartu Tanda Penduduk (KTP) No. ${idCardNo} (untuk selanjutnya disebut sebagai “PIHAK KEDUA atau PENERIMA PINJAMAN”). </p>
            <p>Pihak Pertama dan Pihak Kedua secara bersama-sama disebut sebagai “Para Pihak”. </p>
            <p>Para Pihak dengan ini menerangkan terlebih dahulu hal berikut ini:</p>
            <p>a. Bahwa PT. Glotech Prima Vista merupakan Penyelenggara Layanan yang menyediakan dan mengelola Platform Layanan Pinjaman Uang bagi Pemberi Pinjaman dan Peminjam.</p>
            <p>b. Bahwa Pemberi Pinjaman dan Peminjam adalah orang perorangan yang tunduk pada peraturan hukum di Republik Indonesia, serta memiliki hak dan kapasitas penuh untuk secara independen melaksanakan hak dan kewajiban sesuai dengan syarat dan ketentuan dalam Perjanjian ini.</p>
            <p>c. Bahwa Pemberi Pinjaman dan Peminjam merupakan Pengguna yang telah terdaftar dalam platform layanan milik Penyelenggara Layanan tersebut di atas.</p>
        </div>
    </div>
    <div class="rig_page">
        <div class="left_cotain">
            <p>This Loan Agreement (“Agreement”) is entered on ${signDayEn}, ${signDateEn} by and between:</p>
            <p><span style="font-weight: 700;">1. ${kuasaName} (acting as the Attorney-in-Fact on behalf of ${lenderName} as the Lender)</span>, having its legal domicile at ${kuasaDom}, and Card Holder (ID) No. ${kuasaIDCard} (hereinafter shall be referred to as the “FIRST PARTY”). </p>
            <p>2. ${realName}, individual and an Indonesian Citizen, having its legal domicile at ${liveCity}, and Card Holder (ID) No. ${idCardNo} (hereinafter shall be referred to as the “SECOND PARTY or BORROWER”). </p>
            <p>The First Party and the Second Party hereinafter collectively referred to as “The Parties”. </p>
            <p>WITNESSETH:</p>
            <p><span style="font-weight: 700;">a. WHEREAS</span>, PT. Glotech Prima Vista is a Service Provider that provides and administrates the Loan Service Platform for the Lenders and Borrowers.</p>
            <p><span style="font-weight: 700;">b. WHEREAS</span>, the Lender and the Borrower are individuals subject to the Indonesian Laws, and have the full right and capacity to independently execute their rights and obligations in accordance with the terms and conditions of this Agreement.</p>
            <p><span style="font-weight: 700;">c. WHEREAS</span>, both the Lender and Borrower are the registered Users of such Platform mentioned above.</p>
            <p><span style="font-weight: 700;">d. WHEREAS</span>, based on the Power of Attorney for the Attorney-in-Fact, then the Attorney-in-Fact is authorized to sign the Loan Agreement for the interest of the Lender, and therefore all rights, obligations and liabilities as regulated in this Agreement shall remain between the Lender and the Borrower.</p>
        </div>
    </div>
</div>


<div class="base_page_other">
    <div class="left_page">
        <div class="left_cotain">
            <p>d. Bahwa berdasarkan Surat Kuasa untuk Penerima Kuasa, maka Penerima Kuasa berwenang menandatangani Perjanjian Pinjam Meminjam untuk kepentingan Pemberi Kuasa (Pemberi Pinjaman), dan karenanya seluruh hak, kewajiban dan tanggung jawab sebagaimana diatur dalam Perjanjian ini akan tetap berada di antara Pemberi Pinjaman dan Peminjam.</p>
            <p>e. Bahwa Peminjam hendak mengajukan pinjaman kepada Pemberi Pinjaman dan Pemberi Pinjaman bersedia memberikan pinjaman kepada Peminjam berdasarkan ketentuan perundang-undangan yang berlaku melalui sarana atau layanan berbasis teknologi informasi sebagaimana disediakan oleh Penyelenggara Layanan.</p>
            <p>f. Bahwa sehubungan dengan ditandatanganinya Perjanjian ini oleh Penerima Kuasa dan Penerima Pinjaman, maka PT. Glotech Prima Vista dalam hal ini hanya bertindak dalam kapasitasnya sebagai Penyelenggara Layanan yang menyediakan dan mengelola Platform Layanan Pinjaman Uang bagi Pemberi Pinjaman dan Peminjam.</p>
            <p>Berdasarkan hal-hal tersebut di atas maka Para Pihak dengan ini mengikatkan diri secara hukum untuk membuat Perjanjian ini berdasarkan syarat dan ketentuan sebagai berikut:</p>
            <div class="top_tit">1. DEFINISI</div>
            <p>a. Perjanjian ini juga merujuk pada Perjanjian yang dibuat antara Penyelenggara Layanan dan Pemberi Pinjaman, lampiran-lampiran, berikut seluruh ketentuan atau kontrak yang disepakati oleh dan antara Pemberi Pinjaman dan Peminjam sehubungan dengan penggunaan platform layanan yang disediakan oleh Penyelenggara Layanan, beserta ketentuan-ketentuan lainnya yang disetujui oleh Pemberi Pinjaman dan Peminjam pada saat proses pengajuan pinjaman.</p>
            <p>b. Waktu Mulai Berlakunya Perjanjian merujuk pada waktu dimana permohonan Pinjaman telah disetujui, dan bersamaan dengan diterimanya dana pinjaman oleh Peminjam dari Pemberi Pinjaman melalui Penyelenggara Layanan.</p>
        </div>
    </div>
    <div class="rig_page">
        <div class="left_cotain">
            <p><span style="font-weight: 700;">e. WHEREAS</span>, the Borrower intends to acquire funding (loan) from the Lender and the Lender hereby agrees to provide such loans via the information technology-based facilities or services as provided by the Service Provider under the prevailing Regulations.</p>
            <p><span style="font-weight: 700;">f. WHEREAS</span>, in respect of the signing of this Agreement by the Attorney-in-Fact and the Borrower, in this case PT. Glotech Prima Vista only acts in its capacity as the Service Provider that provides and administrates the Loan Service Platform for the Lenders and Borrowers.</p>
            <p>Now therefore, both Parties have agreed to enter into this Loan Agreement ("Agreement") under the terms and conditions as follows: </p>
            <div class="top_tit">1. DEFINITIONS:</div>
            <p>a. This Agreement also refers to the Agreement made between the Service Provider and the Lender, the appendixes, and all terms or contracts agreed by and between the Lender and the Borrower related to the usage of the Loan Service Platform provided by the Service Provider, along with the other provisions approved by the Lender and Borrower at the time of the loan application process.</p>
            <p>b. The Commencement Date of the Agreement refers to the time at which the Loan application has been approved, and together with the receipt of such funding by the Borrower from the Lender through the Service Provider.</p>
            <p>c. The Maturity Date is the due date to settle all payment obligations as determined together with its interest or penalties and other fees imposed by the Service Provider (if any).</p>
            <p>d. The Financing Value is the whole amount, including all amount listed therein.</p>
        </div>
    </div>
</div>


<div class="base_page_other">
    <div class="left_page">
        <div class="left_cotain">
            <p>c. Tanggal Jatuh Tempo adalah batas waktu penyelesaian kewajiban pinjaman yang telah ditentukan berikut bunga dan denda yang menyertainya serta seluruh biaya lainnya yang dikenakan oleh Penyelenggara Layanan (jika ada).</p>
            <p>d. Nilai Pembiayaan adalah keseluruhan nilai, meliputi seluruh nilai yang tercantum di dalamnya.</p>
            <p>e. Risiko Gagal Bayar adalah risiko yang berasal dari hutang tidak tertagihkan (bad debt) Peminjam. Hutang yang dikategorikan sebagai hutang tak tertagihkan adalah hutang yang berumur > 90 (sembilan puluh) hari.</p>
            <p>f. Informasi Pribadi adalah informasi yang hanya dapat digunakan oleh Penyelenggara Layanan untuk mengidentifikasikan identitas atau data Pemberi Pinjaman dan Peminjam, meliputi tapi tidak terbatas pada Nomor KTP, NPWP, Rekening Bank, Akun Online, Alamat, dan Informasi Pendukung lainnya. Informasi mana merupakan informasi yang tidak dapat diketahui atau diakses baik oleh Pemberi Pinjaman maupun Peminjam kecuali ditentukan lain oleh ketentuan perundang-undangan yang berlaku.</p>
            <p>Terlepas dari ketentuan di atas, untuk keperluan penyelesaian/penagihan kewajiban pembayaran (Bad Debt) sebagaimana dimaksud di atas, Penyelenggara Layanan dapat memberikan Informasi Pribadi Peminjam kepada Pemberi Pinjaman yang diberikan dan diizinkan oleh Peminjam untuk diungkapkan oleh Penyelenggara Layanan.</p>
            
            <div class="top_tit">2. JUMLAH, KETENTUAN PINJAMAN & PEMBAYARANNYA</div>
            <p>a. Nominal Pinjaman yang disetujui untuk Peminjam adalah sebesar Rp ${amount1}. (${amount2}. rupiah). </p>
            <p>b. Untuk sejumlah Pinjaman tersebut di atas, maka Peminjam tunduk pada ketentuan sebagai berikut:</p>
            <p class="npsp">(i) Jangka waktu pinjaman (Tenor) adalah ${borrowingTerm1}, terhitung sejak dana pinjaman telah disetor oleh Pemberi Pinjaman ke Rekening Virtual Pemberi Pinjaman di Penyelenggara Layanan untuk dialokasikan ke Rekening Peminjam.</p>
        </div>
    </div>
    <div class="rig_page">
        <div class="left_cotain">
            <p>e. Default Risk is risk that arises from bad debts of the Borrower. Debt categorized as uncollectible is debt aging > 90 (ninety) days.</p>
            <p>f. Personal Information is information that can only be used by the Service Provider to identify the identity or data of the Lender and Borrower, including but not limited to ID Number, Tax ID Number, Bank Account, Online Account, Address, and other Supporting Information. As such, the Information cannot be known or accessed by either the Lender or the Borrower unless otherwise required by the applicable Laws and Regulations.</p>
            <p>Notwithstanding the foregoing, for the purpose of settlement/collection of payment obligations (Bad Debt) mentioned above, the Service Provider may provide Personal Information of the Borrower to the Lender as provided and permitted by the Borrower to be disclosed by the Service Provider.</p>
        
            <div class="top_tit">2. AMOUNT, TERMS OF LOAN & PAYMENT</div>
            <p>a. The amount of Loan as approved for the Borrower is IDR ${amount1}. (${amount2}. rupiah).</p>
            <p>b. For the Loan mentioned above, the Borrower is subject to these following terms:</p>
            <p class="npsp">(i) The Loan Term is ${borrowingTerm2}, starting from the effective date such Loan has been deposited into the Lender’s Virtual Account at the Service Provider to be allocated to the Borrower’s Account.</p>
            <p class="npsp">(ii) The Loan Interest along with the Service Fee (including the analysis and evaluating services cost, billing and loan collection cost, fees paid for loan disbursement and other expenses) is ${dayRate} per day. Such Interest and Service Fee might be changed based on the First Party’s sole discretion as the Service Provider.</p>
        </div>
    </div>
</div>


<div class="base_page_other">
    <div class="left_page">
        <div class="left_cotain">
            <p class="npsp">(ii) Bunga Pinjaman beserta Biaya Layanan (termasuk dalam hal ini biaya layanan analisis dan evaluasi pinjaman, layanan penagihan dan pengumpulan pinjaman, biaya yang dibayarkan untuk penyaluran dana pinjaman serta biaya lainnya) sebesar ${dayRate} per hari. Besarnya Bunga Pinjaman dan Biaya Layanan ini dapat berubah sesuai dengan pertimbangan PT. Glotech Prima Vista sebagai Penyelenggara Layanan.</p>
            <p class="npsp">(iii) Bunga Pinjaman dan Biaya Layanan mana akan langsung dipotong oleh Penyelenggara Layanan secara seketika pada saat dana pinjaman diberikan oleh Pemberi Pinjaman untuk ditransfer kepada Peminjam melalui Penyelenggara Layanan.</p>
            <p class="npsp">(iv) Pokok Pinjaman wajib dibayar oleh Peminjam (melalui Penyelenggara Layanan) pada tanggal jatuh tempo yang telah ditentukan sesuai dengan Tenor Pinjaman.</p>
            <p class="npsp">(v) Denda atas keterlambatan pembayaran setelah tanggal jatuh tempo kepada Penyelenggara Layanan dengan ketentuan denda sebagai berikut:<br>
                •Bunga keterlambatan akan dikenakan sebesar: 1.6% (satu koma enam persen) per hari dari jumlah pokok pinjaman.</p>
            <p>Adapun maksimum besarnya denda keterlambatan yang dapat dikenakan kepada Peminjam sebagaimana dimaksud pada point (v) di atas adalah 100% dari total Pinjaman yang dicairkan. </p>
        
            <div class="top_tit">3. HAK & KEWAJIBAN PARA PIHAK</div>
            <p>a. BAGI PIHAK PERTAMA: </p>
            <p>Pihak Pertama berhak untuk:</p>
            <p>- Menerima kembali (untuk kepentingan Pemberi Pinjaman) seluruh pembayaran pokok pinjaman dari Peminjam; dan </p>
            <p>- Dalam hal Peminjam tidak memenuhi kewajibannya, maka Pihak Pertama (untuk kepentingan Pemberi Pinjaman) berhak untuk segera mengakhiri Perjanjian ini dan melakukan tindakan yang diperlukan untuk mendapatkan kembali haknya.</p>
        </div>
    </div>
    <div class="rig_page">
        <div class="left_cotain">
            <p>(iii) Such Interest and Service Fee shall be deducted directly at the time the said Loan is transferred by the Lender to the Borrower through the Service Provider.</p>
            <p>(iv) The Principal Loan must be paid by the Borrower (through the Service Provider) on the maturity date according to the Loan Tenor. </p>
            <p>(v) Late Payment Fine (Penalty) after its due date to the Service Provider as follows:<br>
                •Late payment interest charge: 1.6% (one point six percent) per day is payable for the principal loan amount.</p>
            <p>The maximum amount of late payment fine that can be imposed to the Borrower as stated in point (v) above is 100% of the total Loan disbursed.</p>
        
        <div class="top_tit">3.RIGHTS & OBLIGATIONS OF THE PARTIES</div>
            <p>a.FOR THE FIRST PARTY:</p>
            <p>The First Party has the right to:</p>
            <p>- To receive payment (for the Lender’s interest) of the entire principal amount from the Borrower; and</p>
            <p>- In the event that the Borrower fails to fulfill its obligations, then the First Party (for the Lender’s interest) is entitled to immediately terminate this Agreement and to conduct any necessary actions to acquire its rights</p>

            <p>The First Party is obligated to:</p>
            <p>- Select (at its own discretion) the Borrower who will receive loan from the Lender. As such, the First Party shall bear all consequences arising from its own selection;</p>
            <p>In relation to the selection mentioned above, either the Service Provider or its Staff cannot be appointed by the Lender to conduct loan selection process.</p>
        </div>
    </div>
</div>


<div class="base_page_other">
    <div class="left_page">
        <div class="left_cotain">
            <p>Pihak Pertama wajib untuk:</p>
            <p>- Melakukan seleksi (atas pertimbangannya sendiri) atas Peminjam yang akan menerima dana pinjaman dari Pemberi Pinjaman. Dalam hal ini  Pihak Pertama wajib menanggung semua konsekuensi atas pilihannya tersebut; </p>
            <p>Dalam kaitannya dengan seleksi tersebut di atas, baik Penyelenggara Layanan ataupun Staffnya tidak dapat ditunjuk oleh Pemberi Pinjaman untuk melakukan proses seleksi atas Peminjam.</p>
            <p>- Melampirkan copy Surat Kuasa sebagaimana dimaksud pada bagian pendahuluan di atas. Penyelenggara Layanan tidak akan menjalankan instruksi atau memproses pendistribusian dana pinjaman kepada Peminjam apabila Pihak Pertama belum melampirkan copy Surat Kuasa tersebut. Pemberi Pinjaman selanjutnya menyatakan bahwa segala risiko yang timbul sebagai akibat dari pemberian wewenang kepada Penerima Kuasanya tersebut menjadi tanggungan/beban Pemberi Pinjaman sepenuhnya.</p>

            <p>b.BAGI PEMINJAM:</p>
            <p>Peminjam berhak untuk:</p>
            <p>- Menerima pinjaman dari Pemberi Pinjaman melalui Pihak Pertama dengan mengikuti ketentuan bunga, biaya layanan dan/atau denda yang telah disepakati dalam Perjanjian ini; dan</p>
            <p>- Jika diperlukan, mendapatkan akses informasi (melalui Pihak Pertama) untuk mengetahui rincian penggunaan dana beserta pembayaran (posisi) pinjamannya.</p>
            
            <p>Peminjam wajib untuk:</p>
            <p>- Setiap saat mematuhi ketentuan perundang-undangan yang berlaku terkait tindak pidana pencucian uang dan pencegahan pendanaan terorisme;</p>
            <p>- Memberikan kuasa yang  yang tidak bisa dicabut kembali kepada Penyelenggara Layanan untuk melakukan pemotongan bunga pinjaman beserta biaya layanan yang bersifat sekali potong secara langsung pada saat dana pinjaman ditransfer ke Rekening Bank Peminjam;</p>
        </div>
    </div>
    <div class="rig_page">
        <div class="left_cotain">
            <p>- Attach copy of the above-mentioned Power of Attorney. The Service Provider will not carry out the instructions or process the disbursement of loan fund to Borrower if the First Party has not submitted such copy. The Lender further states that all risks arising from the authorization to the Attorney-in-Fact will be entirely borne by the Lender.</p>

            <p>b.FOR THE BORROWER:</p>
            <p>The Borrower has the right to:</p>
            <p>- Receive loan from the Lender through the Service Provider under the terms of the interest rate, service fee and/or penalties agreed in this Agreement; and </p> 
            <p>- If needed, gain access to the information (through the Service Provider) to find out details of its payment positions.</p> 
            
            <p>The Borrower is obligated to:</p>
            <p>- At any time to comply with the prevailing and applicable Laws related to the money laundering and the prevention of terrorism funding; </p>
            <p>- To provide an irrevocable power of attorney to the Service Provider to deduct a one-time Loan Interest and Service Fee directly by the time the said loan is transferred into the Borrower’s Bank Account;</p>
            <p>- At the maturity date as determined, the Borrower will pay: (i) the principal amount of loan to the Lender (through the Service Provider), and/or (ii) fine (if any) to the Service Provider. Borrower fully awares that in the event of any overdue  Borrower also fully awares that in the event of late payment for more than 90 days, then the Service Provider will transfer the collection duties and return back the loan responsibilities directly to the Lender. Consequently, the Service Provider cannot be held liable for any losses whatsoever either suffered by the Lender or Borrower.</p>
        </div>
    </div>
</div>


<div class="base_page_other">
    <div class="left_page">
        <div class="left_cotain">
            <p>- Pada tanggal jatuh tempo yang telah ditentukan: (i) membayar pokok pinjaman kepada Pemberi Pinjaman (melalui Penyelengara Layanan), dan/atau (ii) denda (jika ada) kepada Penyelengara Layanan. Peminjam juga menyadari sepenuhnya bahwa bila terjadi keterlambatan pembayaran lebih dari 90 hari, maka Penyelenggara Layanan akan mengalihkan tugas penagihan dan mengembalikan tanggungjawab terkait dengan pinjaman secara langsung kepada Pemberi Pinjaman. Sebagai konsekuensinya, Penyelenggara Layanan tidak dapat diminta pertanggungjawaban atas kerugian dalam bentuk apapun juga baik yang diderita oleh Pemberi Pinjaman maupun Peminjam.</p>
            <p>- Menjamin bahwa seluruh data pribadi dan dokumen pendukung lainnya yang diberikan oleh Peminjam baik pada saat melakukan pendaftaran layanan ataupun pada saat pengajuan pinjaman dan selama berlakunya Perjanjian ini adalah informasi yang benar dan valid. Setiap perubahan pada data pribadi dan/atau informasi tersebut di atas akan segera diinformasikan kepada Penyelenggara Layanan dalam jangka waktu paling lambat 3 (tiga) hari setelah terjadinya perubahan; </p>
            <p>- Memberikan kuasa kepada Penyelenggara Layanan (atau pihak ketiga yang ditunjuk oleh Penyelenggara Layanan) untuk melakukan verifikasi informasi tersebut, serta mengelola dan menggunakan data atau informasi tersebut sesuai dengan ketentuan dalam Perjanjian ini. Dalam hal ini Peminjam wajib menanggung semua konsekuensi atas keaslian dan kebenaran informasi pribadi yang diberikan;</p>
            <p>- Menjaga status, kredibilitas serta riwayat kredit yang baik dan dapat dipercaya sebagai Pengguna Terdaftar dan Peminjam dalam platform layanan yang disediakan oleh Penyelenggara Layanan selama berlakunya Perjanjian ini;</p>
            <p>- Melakukan pengoperasian pengajuan pinjaman sendiri;</p>
        </div>
    </div>
    <div class="rig_page">
        <div class="left_cotain">
            <p>- Warrant that all personal data and other supporting documents provided by the Borrower either at the time of registration or upon the loan application and during the validity of this Agreement are true and valid. Should there be any changes in the personal data and/or supporting documents mentioned above must be immediately informed to the Service Provider within 3 (three) days after such changes occur;</p>
            <p>- Give authorization to the Service Provider (or the third party designated by the Service Provider, including among others an Information Technology Development Company along with its supporting companies) to verify and analize the information as stated above, and to administrate and use such data or information pursuant to the provisions regulated in this Agreement. As such, the Borrower shall bear all consequences for the authenticity and accuracy of the personal information provided herein; </p>
            <p>- Constantly keep its respectable status, credibility and credit record as a Registered User and Borrower in the Loan Service Platform provided by the Service Provider during the validity of this Agreement;</p>
            <p>- To operate its own loan application;</p>
            <p>- Shall not use such loan for any purposes in violation of the Laws and Regulations, otherwise the Lender at any time might request the Borrower to instantly settle all payments in full. Consequently, the Borrower shall be fully responsible for any money laundering acts or other criminal offenses committed by the Borrower beyond the Lender and the Service Provider’s knowledge; and</p>
        </div>
    </div>
</div>


<div class="base_page_other">
    <div class="left_page">
        <div class="left_cotain">
            <p>- Tidak akan menggunakan dana pinjaman untuk tujuan apapun yang melanggar ketentuan Hukum dan Undang-Undang yang berlaku. Dalam hal terjadi pelanggaran atas ketentuan ini, maka Pemberi Pinjaman setiap saat dapat meminta Peminjam untuk segera menyelesaikan seluruh pembayaran pinjaman secara sekaligus penuh. Sebagai akibatnya, Peminjam akan bertanggung jawab penuh atas tindakan pencucian uang atau tindak pidana lainnya yang dilakukan oleh Peminjam tanpa sepengetahuan Pemberi Pinjaman dan Penyelenggara Layanan; dan</p>
            <p>- Selama berlakunya Perjanjian ini, jika Peminjam melakukan peminjaman dana kepada pihak ketiga lain di luar Perjanjian ini atau menjadi penjamin bagi pihak ketiga dalam peminjaman dana di luar Perjanjian ini, maka Peminjam wajib dalam jangka waktu 3 (tiga) hari kerja memberikan pemberitahuan tertulis pada Penyelenggara Layanan. Sehubungan dengan hal tersebut, Peminjam sepenuhnya hanya bertanggung jawab kepada Pemberi Pinjaman sesuai dengan yang diatur dalam Perjanjian ini. Oleh karena itu, baik Pemberi Pinjaman ataupun Penyelenggara Layanan tidak dapat diminta pertanggung jawabannya atau diminta turut bertanggung jawab terhadap pinjaman dana lainnya atau atas penjaminan bagi pihak ketiga lainnya yang dilakukan oleh Peminjam di luar Perjanjian ini.</p>
        
            <div class="top_tit">4. PELUNASAN LEBIH AWAL:</div>
            <p>Atas pilihannya sendiri dan tanpa dikenakan penalti (denda), Peminjam dapat melakukan pembayaran pinjaman sebelum tanggal jatuh tempo yang telah ditentukan. Namun, dalam hal ini Peminjam menyadari bahwa Bunga Pinjaman penuh yang telah dibayar pada saat awal pencairan dana pinjaman tidak bisa dikembalikan kepada Peminjam.</p>
        </div>
    </div>
    <div class="rig_page">
        <div class="left_cotain">
            <p>- During the validity of this Agreement, should the Borrower lends fund to any third party outside this Agreement or becomes a guarantor for a third party in lending fund outside this Agreement, then the Borrower shall inform the Service Provider in written within 3 (three) working days. As such, the Borrower is only responsible to the Lender pursuant to the terms of this Agreement. Accordingly, neither the Lender nor the the Service Provider shall be held liable or accountable for any other borrowing or the said guarantee for the third parties by the Borrower outside this Agreement.</p>
        
            <div class="top_tit">4. EARLY REPAYMENT </div>
            <p>At its option and no penalty, loan payment can be made by the Borrower before the due date. However, in this case the Borrower awares that the full amount of Interest that had been paid upfront at the time of loan disbursement can not be returned to the Borrower. </p>
        </div>
    </div>
</div>


<div class="base_page_other">
    <div class="left_page">
        <div class="left_cotain">
            <div class="top_tit">5. PELANGGARAN PERJANJIAN </div>
            <p>Dalam kondisi-kondisi berikut Peminjam dianggap telah melanggar Perjanjian, yaitu:</p>
            <p>(i) Peminjam melakukan pelanggaran atas larangan penggunaan dana pinjaman untuk tujuan apapun yang bertentangan dengan ketentuan Hukum dan Undang-Undang yang berlaku;</p>
            <p>(ii) Peminjam tidak dapat memenuhi kewajiban pembayarannya; dan/atau </p>
            <p>(iii) Peminjam (baik dengan sengaja atau tidak) menunda pembayaran.</p>
            <p>Sehubungan dengan hal tersebut di atas, dalam hal terdapat tuntutan hukum maka Peminjam akan bertanggung jawab penuh atas semua biaya dan kerugian yang diderita oleh Pemberi Pinjaman dan/atau Penyelenggara Layanan yang dapat timbul dari kejadian ini.</p>
        
            <div class="top_tit">6. PENGAKUAN & JAMINAN</div>
            <p>a. Masing-masing Pihak telah memiliki semua hak, wewenang, dan kemampuan yang diperlukan untuk melaksanakan semua tanggung jawab dan kewajiban sebagaimana diatur dalam Perjanjian ini, dan oleh karenanya Perjanjian ini akan mengikat kedua belah Pihak terhitung sejak tanggal disetujui dan diterimanya pinjaman.</p>
            <p>b. Kedua belah Pihak menyetujui bahwa kewajiban Pemberi Pinjaman untuk memberikan dana pinjaman dianggap telah terpenuhi setelah Pemberi Pinjaman mengirimkan dana pinjaman ke Rekening Virtual Pemberi Pinjaman di Penyelenggara Layanan untuk dialokasikan ke Rekening Peminjam.</p>
            <p>c. Para Pihak selanjutnya sepakat bahwa nominal pinjaman dan besaran suku bunga yang disetujui adalah sebesar nominal dan suku bunga yang telah melalui proses analisis kredit dan verifikasi oleh Penyelenggara Layanan. Nominal pinjaman yang diterima oleh Peminjam adalah jumlah dari pokok pinjaman dikurangi dengan bunga pinjaman dan biaya layanan.</p>
        </div>
    </div>
    <div class="rig_page">
        <div class="left_cotain">
            <div class="top_tit">5. BREACH OF AGREEMENT</div>
            <p>Under the following conditions the Borrower is deemed to have violated this Agreement:</p>
            <p>(i) The Borrower violates the prohibition to use the loan funds for any purposes against or contrary to the applicable Laws and Regulations;</p>
            <p>(ii) The Borrower fails to complete its payment obligation; and/or.</p>
            <p>(iii) The Borrower (whether intentionally or not) delays its payment obligation.</p>
            <p>In connection with the above conditions, should there be a lawsuit, then the Borrower will be fully responsible to compensate all costs and losses suffered by the Lender and/or the Service Provider that might arise from such event.</p>
        
            <div class="top_tit">6. ACKNOWLEDGEMENT & WARRANTY</div>
            <p>a. Each Party has all the necessary rights, power and capability to enter into and perform all responsibilities and obligations herein, and therefore this Agreement will bind both Parties since the date of approval and acceptance of loan.</p>
            <p>b. Both Parties agree that the Lender’s obligation to provide loan is completed once the Lender transferred such amount of money to the Lender’s Virtual Account at the Service Provider to be allocated to the Borrower’s Account. </p>
            <p>c. Furthermore, the Parties agree that the approved amount of loan and interest rate is the amount of loan and interest rate that have been analyzed and verified by the Service Provider. The exact amount of loan received by the Borrower is the amount of principal loan deducted by the loan interest and service fees. </p>
        </div>
    </div>
</div>


<div class="base_page_other">
    <div class="left_page">
        <div class="left_cotain">
            <p>d. Baik Pemberi Pinjaman maupun Peminjam mengakui bahwa dalam keadaan apapun Penyelenggara Layanan tidak dapat diminta pertanggung jawabannya atas kegagalan setiap Pihak dalam memenuhi hak dan kewajibannya masing-masing. Dalam hal ini Penyelenggara Layanan hanya bertindak dalam kapasitasnya sebagai Penyedia dan Pengelola Platform Layanan Pinjaman Uang. </p>
            <p>e. Syarat dan Ketentuan yang diatur dalam Platform Layanan dapat sewaktu-waktu berubah sesuai dengan kebijakan Pihak Pertama tanpa adanya pemberitahuan secara tertulis terlebih dahulu kepada Para Pihak (Pengguna). Dalam hal ini Pengguna tunduk dan terikat pada syarat dan ketentuan beserta perubahan yang dibuat setelahnya oleh Pihak Pertama.</p>
            <p>f. Pengguna dengan ini menyatakan bahwa: </p>
            <p class="npsp">- Telah membaca, memahami dan menyadari sepenuhnya atas segala risiko yang dapat timbul dari penggunaan platform layanan pinjaman uang; dan </p>
            <p class="npsp">- Menyadari sepenuhnya bahwa meskipun Penyelenggara Layanan berusaha untuk melengkapi Sistem Platform Layanan dengan pengamanan sistem yang sebaik-baiknya menurut penilaian Penyelenggara Layanan, namun Penyelenggara Layanan tidak dapat menjamin sepenuhnya bahwa Sistem akan terbebas dari masalah dan/atau gangguan tersebut. Dalam hal ini Penyelenggara Layanan dibebaskan dari segala bentuk tanggung jawab atau kerugian yang terjadi baik secara langsung maupun tidak langsung, yang diakibatkan oleh segala gangguan, virus komputer, kerusakan jaringan komunikasi, pencurian atau perusakan terhadap Sistem, penggunaan Akun Pengguna dan passwordnya oleh pihak yang tidak berhak, dan/atau sebab-sebab lainnya yang berada di luar kendali Penyelenggara Layanan (Peristiwa Force Majeure).</p>
        </div>
    </div>
    <div class="rig_page">
        <div class="left_cotain">
            <p>d. Both the Lender and the Borrower acknowledge that under no circumstances shall the Service Provider be held liable for any failures of the Parties to fulfill its rights and obligations. As such, the Service Provider only acts in its capacity as the Provider and Administrator of the Loan Service Platform.</p>
            <p>e. The Service Provider at any time may change the Terms and Conditions set forth in the Loan Service Platform at its sole discretion without prior written notice to the Parties (User). Accordingly, the User shall be subject and bound to the terms and conditions and any amendments made afterwards by the Service Provider.</p>
            <p>f. The User hereby state that:</p>
            <p class="npsp">- Have read and fully understand and aware of the risks that may arise from using this Loan Service Platform; and;</p>
            <p class="npsp">- Fully aware that despite the Service Provider’s effort (at its best) to complete its security system according to the Service Provider’s assessment, the Service Provider cannot fully guarantee that the System will be free from any problems and/or interferences. As such, the Service Provider shall be discharged from any liabilities or losses incurred directly or indirectly, as a result from any interruption, computer virus, damage to the communication network, theft or damage to the System, unauthorized access on the User‘s Account and password, and/or other causes beyond the control of the Service Provider (Force Majeure Events).</p>
        </div>
    </div>
</div>


<div class="base_page_other">
    <div class="left_page">
        <div class="left_cotain">         
            <div class="top_tit">7. TINDAK PIDANA</div>
            <p>Jika dalam proses pemberian dan pengajuan pinjaman, Pengguna memberikan informasi palsu atau menyembunyikan informasi penting yang bersifat ilegal, maka Pihak Pertama berhak melaporkannya kepada Otoritas yang berwenang.</p>
                
            <div class="top_tit">8. PENGALIHAN HAK & KEWAJIBAN </div>
            <p>a. Peminjam tidak dapat mengalihkan hak dan kewajibannya sebagaimana dimaksud dalam Perjanjian ini kepada Pihak Ketiga manapun tanpa ada persetujuan tertulis terlebih dahulu dari Pemberi Pinjaman dan Penyelenggara Layanan.</p>
            <p>b. Terlepas dari ketentuan di atas, Penyelenggara Layanan (tanpa memerlukan persetujuan dari Pemberi Pinjaman dan Peminjam) dapat mengalihkan hak dan kewajibannya kepada Pihak Ketiga sepanjang untuk memenuhi persyaratan konsolidasi, akuisisi, merger, ataupun corporate action lainnya.</p>
                
            <div class="top_tit">9. BERAKHIRNYA PERJANJIAN</div>
            <p>a. Perjanjian ini secara otomatis berakhir pada tanggal jatuh tempo pembayaran pinjaman, dengan ketentuan bahwa seluruh pembayaran pokok pinjaman beserta dendanya (jika ada) telah diselesaikan oleh Peminjam sebagaimana ditetapkan. </p>
            <p>b. Penyimpangan dan pelanggaran oleh Pengguna (Pemberi Pinjaman dan/atau Peminjam) terhadap ketentuan dalam Perjanjian ini dapat berakibat pada berakhirnya Perjanjian.</p>
            <p>c. Baik Penyelenggara Layanan maupun Pemberi Pinjaman setiap saat dapat mengakhiri Perjanjian, apabila:</p>
            <p class="npsp">- Peminjam gagal untuk memenuhi kewajiban pembayaran sesuai dengan Perjanjian ini;</p>
            <p class="npsp">- Peminjam melarikan diri sebelum tanggal jatuh tempo pengembalian pinjaman, menolak berkomunikasi, menyangkal keberadaan transaksi pinjaman, atau melakukan tindakan non-kooperatif dan berbahaya lainnya;</p>
        </div>
    </div>
    <div class="rig_page">
        <div class="left_cotain">
            <div class="top_tit">7. CRIMINAL OFFENSES </div>
            <p>In respect of this subscription and loan application process, if the User provide false information or hides any material (important) information that is illegal, then the First Party is entitled to report such criminal offenses to the competent Authority.</p>
            
            <div class="top_tit">8. TRANSFER OF RIGHTS & OBLIGATIONS </div>
            <p>a. The Borrower cannot transfer any of its rights and obligations as stated in this Agreement to any third Party without prior written consent from the Lender and the Service Provider.</p>
            <p>b. Notwithstanding the foregoing, the Service Provider (without obtaining any consent from the Lender and the Borrower) may transfer its rights and obligations to the Third Parties in order to fulfill the consolidation, acquisition, merger and/or other corporate actions requirements.</p>
            
            <div class="top_tit">9. TERMINATION OF AGREEMENT</div>
            <p>a. This Agreement shall automatically end on the due date of the loan payment, provided that all payments of the principal loan along with the penalties (if any) therein have been settled by the Borrower as determined.</p>
            <p>b. Any violation on the provisions of this Agreement by the User (Lender and/or Borrower) may result in the termination of the Agreement.</p>
            <p>c. Either the Service Provider or the Lender at any time may terminate this Agreement, in the following matters:</p>
            <p class="npsp">- The Borrower fails to complete its payment in accordance with this Agreement;</p>
            <p class="npsp">- The Borrower flees before the payment due date, refuses to communicate, denies the existence of the loan, or conducts other non-cooperative and dangerous actions;</p>
        </div>
    </div>
</div>


<div class="base_page_other">
    <div class="left_page">
        <div class="left_cotain">
            <p class="npsp">- Dalam jangka waktu Perjanjian dan sebelum tanggal jatuh tempo pembayaran, berdasarkan penilaian logis dari Penyelenggara Layanan atau Pemberi Pinjaman, bahwa Peminjam dinyatakan tidak mampu mengembalikan pinjaman sepenuhnya;</p>
            <p class="npsp">- Peminjam memberikan informasi, data atau dokumen palsu, menyembunyikan informasi penting, atau tidak memberikan informasi mengenai perubahan informasi pribadi sebagaimana dimaksud di atas kepada Penyelenggara Layanan dalam jangka waktu 3 (tiga) hari setelah perubahan informasi tersebut;</p>
            <p class="npsp">- Peminjam dalam jangka waktu 3 (tiga) hari kerja tidak menginformasikan secara tertulis kepada Penyelenggara Layanan jika Peminjam melakukan pinjaman atau menjadi penjamin bagi pinjaman lain yang berada di luar Perjanjian ini sebagaimana telah disebut di ketentuan Pasal 3 huruf (b) di atas; dan/atau </p>
            <p class="npsp">- Terjadi penyitaan, pengambilalihan, penahanan, pembekuan, atau hal-hal lainnya terhadap harta milik Peminjam yang dapat mempengaruhi kemampuan Peminjam dalam melaksanakan tanggungjawabnya sesuai dengan yang diatur dalam Perjanjian ini, dimana Peminjam gagal secara tepat waktu untuk menginformasikan secara tertulis atau memberikan solusi yang efektif atas hal tersebut kepada Penyelenggara Layanan dan Pemberi Pinjaman.</p>
            <p>d. Apabila Perjanjian ini diakhiri oleh sebab apapun, maka segala hak dan kewajiban Pengguna (Pemberi Pinjaman dan Peminjam), termasuk dalam hal ini hak Penyelenggara Layanan, yang sudah timbul sebelum berakhirnya Perjanjian wajib untuk diselesaikan sebagaimana mestinya.</p>
            <p>e. Mengenai pembatalan dan/atau pengakhiran Perjanjian ini, Para Pihak sepakat untuk mengesampingkan ketentuan dalam pasal 1266 Kitab Undang-Undang Hukum Perdata yang berlaku di Republik Indonesia.</p>
        </div>
    </div>
    <div class="rig_page">
        <div class="left_cotain">
            <p class="npsp">- Within the term of the Agreement and before the payment due date, based on the logical assessment of the Service Provider or the Lender, that the Borrower is declared unable to fully settle the loan.</p>
            <p class="npsp">- The Borrower provides false information, data or documents, hides material information, or has not informed any changes on the personal data mentioned above within 3 (three) days since such changes occur to the Service Provider;</p>
            <p class="npsp">- The Borrower within 3 (three) working days has not informed the Service Provider in written regarding the other loan or its position as the Guarantor for another loan outside this Agreement as stated in Article 3 point (b) above; and/or</p>
            <p class="npsp">- Should there be any seizure, acquisition, detention, suspension or other matters of the Borrower's property which may affect the Borrower's ability to perform its responsibilities in accordance with the terms of this Agreement, at which the Borrower fails in a timely manner to inform in writing or to provide effective solutions upon such matters to the Service Provider and the Lender.</p>
            <p>d. Should the Agreement is terminated for whatsoever reasons, then the remaining rights and obligations of the User (the Lender and the Borrower), including the Service Provider’s rights, must be completely settled accordingly before such termination.</p>
            <p>e. Regarding the termination of this Agreement, both Parties agree to waive the Provisions of Article 1266 of the Indonesian Civil Code.</p>
        </div>
    </div>
</div>


<div class="base_page_other">
    <div class="left_page">
        <div class="left_cotain">
            <div class="top_tit">10. KETENTUAN KERAHASIAAN</div>
            <p>a. Selama masa berlakunya dan setelah berakhirnya Perjanjian ini, masing-masing Pengguna dan Penyelenggara Layanan tidak diperkenankan untuk mengungkapkan kepada Pihak Ketiga tentang Perjanjian ini dan/atau informasi lainnya yang berkaitan dengan Perjanjian ini tanpa persetujuan tertulis dari Pihak lainnya, kecuali sebagaimana dipersyaratkan oleh Ketentuan Hukum dan Peraturan Perundang-undangan yang berlaku, Ketetapan Pengadilan dan/atau Instansi Pemerintah yang berwenang. </p>
            <p>b. Tanpa mengurangi ketentuan di atas, sesuai dengan syarat dan ketentuan pengajuan aplikasi atau pendaftaran sebagai Pemberi Pinjaman dan Peminjam dalam Platform Layanan Pinjaman Uang, Penyelenggara Layanan akan menjaga kerahasiaan seluruh data dan informasi terkait lainnya yang telah disampaikan oleh masing-masing Pemberi Pinjaman dan Peminjam.</p>
            <p>c. Dalam rangka mendukung implementasi Fintech Data Center (FDC) AFPI, maka sesuai dengan Surat Direktur Pengaturan, Perizinan dan Pengawasan Fintech Otoritas Jasa Keuangan No. S-582/NB.213/2019 pada tanggal 28 Oktober 2019 mengenai Kewajiban Kontribusi dan Mendukung Implementasi Fintech Data Center (FDC) AFPI, maka PT. Glotech Prima Vista (Penyelengara Layanan) diwajibkan mengungkapkan data Penerima Pinjaman untuk disimpan pada Fintech Data Center (FDC) AFPI.</p>

            <div class="top_tit">11. FORCE MAJEURE</div>
            <p>a. Masing-masing Pihak tidak dapat diminta pertanggungjawaban atas setiap kegagalan atau keterlambatan dalam memenuhi baik sebagian maupun seluruh kewajibannya yang disebabkan oleh adanya kejadian bencana alam, pelaksanaan perintah atau pembatasan dari Pemerintah, dan/atau kejadian lainnya yang berada di luar kendali atau kuasa Para Pihak (Force Majeure).</p>
        </div>
    </div>
    <div class="rig_page">
        <div class="left_cotain">
            <div class="top_tit">10. CONFIDENTIALITY CLAUSE</div>
            <p>a. Without prior written consent from the other Party, each User and the Service Provider shall not disclose to any third Parties this Agreement and/or any information related to this Agreement during the validity of this Agreement, unless as may be required by the relevant Laws and Regulations, Court Rules and/or the Authorized Government Agency. </p>
            <p>b. Notwithstanding the foregoing, in accordance with the terms and conditions at the time of application submission or registration as a Lender and Borrower in the Loan Service Platform, the Service Provider will keep the confidentiality of all data and other relevant information submitted by the Lender and Borrower respectively.</p>
            <p>c. In order to support the implementation of Fintech Data Center (FDC) AFPI, PT. Glotech Prima Vista (Service Provider) is required to disclose Borrower’s data to be stored in FDC pursuant to the Letter of the Director of Regulation, Licensing and Supervision of the Fintech Financial Services Authority No. S-582/NB.213/2019 dated October 28th, 2019 regarding the Obligations of Contributing and Supporting the Implementation of Fintech Data Center (FDC) AFPI.</p>
            
            <div class="top_tit">11. FORCE MAJEURE </div>
            <p>a. Either party shall not be held responsible for any failure or delay to perform all or any part of the obligations due to natural disasters, Government orders or restriction, and/or any other events beyond the control and/or ability of the Parties (Force Majeure). </p>
        </div>
    </div>
</div>


<div class="base_page_other">
    <div class="left_page">
        <div class="left_cotain">
            <p>b. Dalam hal terjadi Force Majeure, maka Pihak yang terkena musibah harus segera memberitahukan pada kesempatan pertama kepada Pihak lainnya yang disusul dengan pemberitahuan tertulis selambat-lambatnya dalam waktu 3x24 jam sejak terjadinya Force Majeure tersebut dan harus dapat membuktikan bahwa keterlambatan atau tidak terlaksananya ketentuan dalam Perjanjian ini adalah sebagai akibat langsung dari Force Majeure.</p>
            <p>c. Penyelesaian seluruh hak dan kewajiban Para Pihak akan tetap dipenuhi sesuai dengan ketentuan yang diatur dalam Perjanjian ini segera setelah peristiwa Force Majeure berakhir.</p>

            <div class="top_tit">12. HUKUM YANG BERLAKU</div>
            <p>Perjanjian ini dan seluruh ketentuan di dalamnya harus ditafsirkan sepenuhnya dan diatur dalam segala halnya sesuai dengan ketentuan perundang-undangan yang berlaku di Indonesia.</p>
            
            <div class="top_tit">13. PENYELESAIAN PERSELISIHAN</div>
            <p>Segala perselisihan di antara Pengguna (termasuk dalam hal ini Penyelenggara Layanan) yang dapat timbul sebagai akibat dari Perjanjian ini wajib untuk diselesaikan secara musyawarah terlebih dahulu dalam jangka waktu 30 (tiga puluh) hari kalender. Jika musyawarah tidak tercapai, maka Para Pihak sepakat untuk menyelesaikannya melalui Badan Arbitrase Nasional Indonesia (BANI). Hasil penyelesaian dan putusan tersebut adalah final dan mengikat kedua belah Pihak. Seluruh biaya yang timbul dalam penyelesaian sengketa sebagaimana dimaksud di atas akan dibebankan kepada Pihak sesuai dengan keputusan Arbitrase.</p>
        </div>
    </div>
    <div class="rig_page">
        <div class="left_cotain">
            <p>b. Within 3x24 hours since Force Majeure occurred, the effected Party shall inform the other Parties immediately followed by a written notice to verify that the related delays and/or non-performances thereof as regulated herein are resulted from a direct effect of Force Majeure.</p>
            <p>c. All rights and obligations of the Parties must be fulfilled according to the terms specified in this Agreement once the Force Majeure ends.</p>

            <div class="top_tit">12. GOVERNING LAW</div>
            <p>This Agreement shall be construed in accordance with and governed in all respects by the Laws of the Republic of Indonesia.</p>
                
            <div class="top_tit">13. DISPUTES RESOLUTION</div>
            <p>All disputes arising in connection herewith shall be settled amicably by the User (including the Service Provider) in good faith within 30 (thirty) calendar days. If the related issues could not be resolved accordingly, then it shall be finally settled under the rules of Badan Arbitrase Nasional Indonesia (The Indonesian National Board of Arbitration or “BANI”). The Arbitration award shall be final and binding upon the Parties. All fees, costs and expenses incurred in such proceedings shall be borne by the Party according to the Arbitration’s verdict.</p>
        </div>
    </div>
</div>


<div class="base_page_other">
    <div class="left_page">
        <div class="left_cotain">
            <div class="top_tit">14. KETENTUAN LAIN-LAIN</div>
            <p>a. Apabila oleh sebab suatu hal dan lainnya yang mengakibatkan Penyelenggara Layanan tidak dapat melanjutkan kegiatan operasionalnya, maka berdasarkan kesepakatan bersama secara tertulis Para Pihak dapat mengakhiri Perjanjian ini dan kemudian mengikuti Prosedur yang berlaku di Penyelenggara Layanan. Dalam hal ini Penerima Pinjaman wajib untuk tetap memenuhi kewajiban pembayaran sebagaimana mestinya. Untuk selanjutnya Para Pihak tunduk pada ketentuan mengenai Berakhirnya Perjanjian yang diatur dalam Perjanjian ini sepanjang segala hak dan kewajiban seluruh Pihak telah diselesaikan.</p>
            <p>b. Hal-hal lain yang tidak atau belum cukup diatur dalam Perjanjian ini akan diputuskan dan kemudian dituangkan secara tertulis sebagai suatu tambahan Perjanjian (Addendum). Penambahan dan/atau Perubahan mana merupakan kesatuan dan menjadi bagian yang tidak dapat dipisahkan dari Perjanjian ini.</p>
            <p>c. Perjanjian ini ditulis dan dilaksanakan dalam bahasa Inggris dan bahasa Indonesia dan setiap versi bahasa akan memiliki efek hukum yang sama. Jika ada perbedaan atau perselisihan diantara versi Bahasa Inggris dan Bahasa Indonesia, maka versi Bahasa Indonesia yang akan berlaku.</p>
            <p>d. Jika terdapat salah satu ketentuan dari Perjanjian ini ternyata tidak sah, dianggap bertentangan atau tidak dapat dilaksanakan dalam hal apapun berdasarkan ketentuan Undang-Undang yang berlaku, maka:</p>
            <p class="npsp">- Para Pihak sepakat untuk menyesuaikan ketentuan dan/atau persyaratan tersebut dalam Perjanjian ini sesuai dengan ketentuan Hukum yang berlaku; dan</p>
        </div>
    </div>
    <div class="rig_page">
        <div class="left_cotain">
            <div class="top_tit">14. OTHER PROVISIONS</div>
            <p>a. If for any reason the Service Provider is unable to continue its operational activities, then the Parties may, by mutual agreement in writing, terminate this Agreement, and subsequently follows the Procedures applicable at the Service Provider. In this case, the Borrower must still fulfil its payment obligations accordingly. Furthermore the Parties are subject to the termination provision regulated herein provided that all rights and obligations of the Parties have been completed.</p>
            <p>b. Any other matters which are not sufficiently governed herein will be further determined in an Addendum mutually agreed and signed by both Parties and shall be an integral part and cannot be separated from this Agreement.</p>
            <p>c. This Agreement is written and executed in English and Indonesian language and each language version shall have the equal legal effect. In case of any discrepancies or conflict between the English and Indonesian language versions, then the Indonesian version will prevail.</p>
            <p>d. Should there be any provisions contained in this Agreement deemed invalid, unlawful or unenforceable in any respect under any applicable Law, then:</p>
            <p class="npsp">- Both Parties hereby agree to revise any provisions and / or requirements in this Agreement according to the prevailing Laws; and</p>
        </div>
    </div>
</div>


<div class="base_pagetwo">
    <div class="left_page">
        <div class="left_cotain">
            <p class="npsp">- Keabsahan ketentuan lain dalam Perjanjian tidak akan terpengaruh, dan karenanya Perjanjian ini tetap berlaku dengan sah.</p>
        </div>
    </div>
    <div class="rig_page">
        <div class="left_cotain">
            <p class="npsp">- The validity of the remaining provisions contained therein shall not in any way be affected or impaired, and this Agreement shall be construed as of such invalid, unlawful or unenforceable provision had never been contained therein.</p>
        </div>
    </div>
</div>


<div class="base_pageone">
    <div class="qwe">Demikian Perjanjian ini dibuat dalam 2 (dua) rangkap yang masing masing rangkapnya mempunyai kekuatan hukum yang sama, pada hari dan tanggal sebagaimana disebut di atas.</div>
    <div class="qwe">IN WITNESS WHEREOF, the Parties have executed this Agreement and made in 2 (two) copies, each of which will be deemed as an original instrument, as of the day and date first written above.</div>

    <div class="qianmi"><span style="float: left;margin-left: 30px;">THE FIRST PARTY,</span><span style="float: right; margin-right: 30px;">THE SECOND PARTY,</span></div>
    <div class="qianmi1"><span style="float: left;margin-left: 30px;">............................</span><span style="float: right; margin-right: 50px;">............................</span></div>
</div>
</div>

<div class="base_page_empty">
</div>

<div class="base_page_other2">
    <div class="twotit">KEBIJAKAN PRIVASI <br>
        PRIVACY POLICY</div>
    <div class="t_qwe">
        A.Ketentuan Umum
        General Terms
    </div>

    <p class="con_two">
        1.PT. Glotech Prima Vista ("Do-It") adalah Perusahaan Penyelenggara Layanan Pinjam Meminjam Uang Berbasis Teknologi Informasi yang menyediakan, mengelola, dan mengoperasikan Layanan Pinjam Meminjam Uang Berbasis Teknologi Informasi berdasarkan Peraturan Otoritas Jasa Keuangan Nomor 77/POJK.01/2016 tentang Layanan Pinjam Meminjam Uang Berbasis Teknologi Informasi ("POJK 77").

        PT. Glotech Prima Vista (“Do-It”) is a Company engages in information-technology based loan services that provides, administrate and operate the Information-Technology Based Loan Services under the Financial Services Regulation No. 77/POJK.01/2016 on the Information Technology-Based Lending Services ("POJK 77").
    </p>
    <p class="con_two">
        2.Layanan Pinjam Meminjam Uang Berbasis Teknologi Informasi ("Layanan") adalah layanan jasa keuangan untuk mempertemukan pemberi pinjaman dan penerima pinjaman dalam rangka pemberian dana pinjaman dalam mata uang rupiah secara langsung melalui sistem elektronik dengan menggunakan jaringan internet.

        The Information-Technology Based Loan Services (“Service”) is a financial service that directly meets a Lender and a Borrower in order to provide loan funds in Rupiah through electronic system using an internet network.
    </p>
    <p class="con_two">
        3.Layanan ini disediakan Do-It sesuai dengan POJK, kebijakan, aturan dan/atau prosedur internalnya yang dapat berubah dari waktu ke waktu atas pertimbangannya sendiri tanpa perlu memperoleh persetujuan dari pihak manapun ("Layanan Do-It").

        This Service is provided by Do-It in accordance with POJK, its policies, rules dan/or internal procedure that might be revised from time to time at its sole discretions without requiring any consents from any parties (“Do-It’s Service”).
    </p>
    <p class="con_two">
        4.Layanan ini disediakan Do-It melalui Platform Do-It. Platform Do-It adalah (a) portal web dan/atau versi mobile dari portal web yang dibuat, dimiliki dan dioperasikan oleh Do-It yang saat ini terletak di dan dapat di akses pada URL berikut: www.do-it.id berikut perubahan URL tersebut dari waktu ke waktu; dan/atau (b) aplikasi mobile dari www.do-it.id yang dibuat, dimiliki dan dioperasikan oleh Do-It, termasuk android berikut perubahannya dari waktu ke waktu.

        This Service is provided by Do-It through Do-It’s Platform. Do-It’s Platform is (a) a web portal and/or mobile version of the web portal created, owned and operated by Do-It which is currently located and accessible at the following URL: www.do-it.id along with its changes from time to time; and/or (b) the mobile applications of www.do-it.id which is created, owned and operated by Do-It, including android and the changes from time to time.
    </p>
    <p class="con_two">
        5.Prosedur Manajemen Data Pribadi ("PMDP") wajib untuk selalu diperhatikan dan dipatuhi, serta dianggap telah dibaca, dipahami sepenuhnya oleh Pengguna, dan karenanya berlaku mengikat bagi setiap Pengguna sebagaimana dimaksud dalam Pasal 1 ayat 9 POJK 77, yaitu Pemberi Pinjaman dan Penerima Pinjaman yang menggunakan Layanan Do-It atau memanfaatkan Platform Do-It. Keberlakuan dan daya mengikat secara hukum dari PMDP ini terhadap Pengguna adalah:

        The Personal Data Management Procedure must always be observed and complied with, and shall be deemed to have been read and fully understood by the User, and therefore bind every User as mentioned in Article 1 paragraph 9 POJK 77, namely the Lender and the Borrower who use Do-It’s Services or utilize Do-It’s Platform. The validity and legal binding power of this PMDP to the User are:
    </p>
    <p class="con_two">
        a.Dalam konteks Pemberi Pinjaman, yaitu selama Pemberi Pinjaman:

        In the context of the Lender, provided that the Lender is
    </p>
</div>

<div class="base_page_other">
    <p class="con_two">
        (i)Masih terdaftar sebagai "Pelanggan" pada Platform Do-It berdasarkan Perjanjian Berlangganan Do-It yang merupakan dasar hukum yang mengawali, meresmikan dan mengikat Pemberi Pinjaman secara hukum sebagai “Pengguna” Layanan Do-It berdasarkan POJK 77 ("Perjanjian Berlangganan"); dan/atau

        Still registered as a “User” on Do-It’s Platform based on the Subscription Agreement which is the legal basis that initiates, formalizes and binds the Lender legally as a User of Do-It’s Service in accordance with POJK 77 (“Subscription Agreement”); and/or
    </p>
    <p class="con_two">
        (ii)Masih memiliki hak tagih atas pengembalian, pembayaran dan pelunasan piutangnya dari Penerima Pinjaman berdasarkan Perjanjian Pinjam Meminjam berikut perubahannya dari waktu ke waktu (termasuk dokumen pendukung, surat pernyataan, persetujuan, non-disclosure agreement, dan jaminan lainnya ("Perjanjian Pinjam Meminjam").

        Still has the rights to collect repayment from the Borrower based on the Loan Agreement along with its changes from time to time (including all supporting documents, statement letters, consents, non-disclosure agreements, and other warranties (“Loan Agreement”).
    </p>
    <p class="con_two">
        Sehubungan dengan hal tersebut di atas, ditegaskan bahwa Do-It tidak bertanggung jawab atas segala risiko gagal bayar dan/atau kerugian lainnya yang dapat diderita oleh Pemberi Pinjaman sebagai akibat dari adanya keterlambatan pembayaran pinjaman dan/atau hutang tidak tertagihkan (bad debt). Adapun yang dimaksud dengan Bad Debt adalah hutang yang berumur ≥ 90 hari.

        In connection with the above-mentioned, Do-It specifically affirms that Do-It is not responsible for any default risks and/or other losses suffered by the Lender as a result from the late payment and/or bad debt. Bad Debt means uncollectible debt aging 90 (ninety) days and above.
    </p>
    <p class="con_two">
        b.Dalam konteks Penerima Pinjaman, yaitu selama Penerima Pinjaman:

        In the context of the Borrower, provided that the Borrower is

        (i)Masih terdaftar sebagai "Pengguna" Layanan Do-It;

        Still registered as a “User” on Do-It’s Platform. <br><br>

        (ii)Masih memiliki kewajiban, tanggung jawab finansial, hutang dan/atau hal sejenisnya terhadap Pemberi Pinjaman berdasarkan Perjanjian Pinjam Meminjam; dan/atau

        Still has the obligation, financial responsibilities, debt and/or similar responsibilities to the Lender based on the Loan Agreement; and/or <br><br>

        (iii)Masih dalam proses pengajuan permohonan perolehan pinjaman berikut asesmen atau verifikasi yang dilakukan oleh Do-It atau perwakilannya. <br><br>

        Still in the process of loan application along with the assessment or verification conducted by Do-It or its representatives.
    </p>
    <p class="con_two">
        B.Ketentuan Khusus Lainnya
        Other Particular Terms

        1.Ruang Lingkup
        The Scope

        Ruang Lingkup atau cakupan definisi "Data Pribadi" meliputi segala data, keterangan, informasi dan/atau dokumen Pengguna beserta pihak terkait Pengguna (keluarga, rekan, karyawan, perusahaan atau penyedia jasa bagi Pengguna) termasuk data, informasi dan dokumen terkait atau turunannya yang:

        The scope of Personal Data includes all data, remarks, information and/or User’s documents and its related parties (family, colleagues, employees, companies or service providers for the Users) including the data, information and related documents or derivatives that
    </p>
</div>

<div class="base_page_other">
    <p class="con_two">
        a.Dipersyaratkan atau diperoleh Do-It dari Pengguna atau diserahkan, diungkapkan atau diunduh oleh Pengguna melalui Platform Do-It atau sumber daya Do-It; <br> <br>

        Required or obtained by Do-It form the User or submitted, disclosed, or downloaded by the User through Do-It’s Platform or Do-It’s resources.  <br><br>

        b.Diperoleh atau diakses secara sah oleh Do-It berdasarkan kebijakan dan prosedur internal Do-It terkait Layanan Do-It selama Pengguna masih terikat pada PMDP ini; dan/atau <br><br>

        Legally acquired or accessed by Do-It based on the internal policy and procedures of Do-It regarding Do-It’s Services as long as the User is still bound to this PMDP; and/or <br><br>

        c.Wajib diserahkan atau telah diberikan oleh Pengguna berdasarkan ketentuan Perjanjian Berlangganan dan/atau Perjanjian Pinjam Meminjam, atau yang secara sukarela diberikan dari waktu ke waktu. <br><br>

        Must be submitted or has been provided by the User based on the provisions of the Subscription Agreement and/or Loan Agreement, or that voluntarily provided from time to time. <br><br>
    </p>
    <p class="con_two">
        2.Do-It berhak meminta, memperoleh, mengumpulkan, mengolah, menyimpan, dan menggunakan Data Pribadi Pengguna dalam rangka atau untuk keperluan: <br><br>

        Do-It is entitled to request, obtain, collect, process, store and use the Personal Data of User in order to or for the purpose of <br><br>

        a.Asesmen, analisis, verifikasi, validasi atau pemeriksaan terhadap (i) permohonan dan/atau aplikasi pinjaman, (ii) proses atau permohonan pendaftaran sebagai Pengguna, dan/atau (iii) profil Pengguna; <br><br>

        Assessment, analysis, verification, validation or examination of (i) loan applications, (ii) the process of or registration application as a User, and/or (iii) the User’s profile. <br><br>

        b.Pemenuhan hak dan kewajiban Do-It berdasarkan ketentuan hukum dan peraturan perundang-undangan, Perjanjian Berlangganan dan/atau Perjanjian Pinjam Meminjam;  <br><br>

        To fulfill the rights and obligations of Do-It based on the Laws and Regulations, Subscription Agreement and/or Loan Agreement. <br><br>

        c.Pengawasan atas pelaksanaan Perjanjian Berlangganan dan/atau Perjanjian Pinjam Meminjam;

        To supervise the implementation of the Subscription Agreement and/or the Loan Agreement. <br><br>

        d.Pelaksanaan kegiatan usaha dan operasional Do-It dari waktu ke waktu dengan itikad baik;

        To conduct business activities and operational of Do-It from time to time in good faith. <br><br>

        e.Survey, riset, evaluasi dan/atau pengembangan produk atau layanan oleh Do-It atau pihak terkait yang berkepentingan; dan/atau

        Survey, research, evaluation and/or development of products and services by Do-It or other related parties.

        f.Penegakan hukum dan kepatuhan Do-It terhadap Peraturan OJK, ketentuan hukum dan peraturan perundang-undangan terkait lainnya.

        The Law enforcement and Do-It’s compliance with OJK’s Regulations, Laws and other related Regulations. <br>


        Tujuan-tujuan di atas tetap menjadi dasar hukum bagi Do-It untuk menyimpan, mengolah dan menggunakan Data Pribadi Pelanggan sekalipun Pelanggan tidak lagi terikat pada PMDP ini, dan untuk menggunakan, menyerahkan atau mengungkapkannya dengan itikad baik kepada: <br>

        The above-mentioned purposes remain the legal basis for Do-It to store, process and use the User’s Personal Data despite the User is no longer bound to this PMDP, and to use, submit or disclose in good faith to <br><br>
    </p>
</div>

<div class="base_page_other">
    <p class="con_two">
        (i)Bank, biro penilai kredit, agen penagihan atau penyedia jasa pihak ketiga lain yang berkepentingan selama terikat pada suatu tanggung jawab menjaga kerahasiaan atas Data Pribadi Pelanggan; <br><br>

        Banks, credit assessment bureaus, collection agencies or other third party service provider as long as such parties are bound by the Confidentiality obligations of the User’s Personal Data.

        (ii)Pihak manapun yang terkait atau merupakan bagian dari Do-It dan berada dalam tanggung jawab menjaga kerahasiaan Data Pribadi Pelanggan untuk tujuan tertentu; dan/atau

        Any Parties related to or part of Do-It and is responsible for maintaining the confidentiality of the User’s Personal Data for certain purposes; and/or  <br><br>

        (iii)OJK, Pengadilan, Kejaksaan, Kepolisian, Instansi yang berwenang lainnya sebagaimana diwajibkan oleh ketentuan hukum dan peraturan perundang-undangan, perintah pengadilan, instruksi atau kebijakan institusi pemerintah atau pejabat pemerintah.

        OJK, Courts, District Attorneys, Police, other authorized Institutions as required by the laws and regulations, court orders, instructions or policies of the government agencies or officials. <br>
    </p>
    <div class="t_qwe">
        C.Cara Perolehan, Pengumpulan dan Penyimpanan Data <br> Pribadi
        How to Obtain, Collect and Store Personal Data
    </div> <br> <br>
    <p class="con_two">
        a.Do-It berhak memperoleh, mengumpulkan dan menyimpan Data Pribadi (secara terinkripsi) baik yang diperoleh melalui Platform Do-It, telepon, alat, sistem atau sumber daya Do-It, afiliasi, pihak terkait atau perwakilan Do-It, maupun yang diakses Do-It atau diajukan oleh Anda melalui sistem, perangkat keras (hardware), perangkat lunak (software) atau peralatan (devices) lainnya yang Do-It atau Pengguna gunakan selama masih memanfaatkan Layanan Do-It atau terdaftar dalam Platform Do-It.  <br> <br>

        Do-It reserves the right to obtain, collect and store Personal Data (encrypted) either acquired through Do-It’s Platform, phones, devices, system or resources of Do-It, affiliates, related parties or Do-It’s representatives, or accessed by Do-It or submitted by the User through the system, hardware, software or other devices that Do-It or the User use while still utitilizing Do-It’s Service or registered on Do-It’s Platform. <br> <br>

        b.Data Pribadi akan disimpan oleh Do-It sekurang-kurangnya selama 5 (lima) tahun sesuai ketentuan pasal 15 ayat (3) Peraturan Menteri Komunikasi dan Informatika Nomor 20 Tahun 2016 tentang Perlindungan Data Pribadi dalam Sistem Elektronik. Do-It akan melakukan penyimpanan ini sesuai kebijakan atau diskresi pribadinya, baik melalui sistem Do-It atau sistem, server ataupun database pihak ketiga lain sebagai penyedia jasa di dalam wilayah Republik Indonesia, yang ditunjuk oleh Do-It ("Penunjukan Pihak Ketiga untuk Penyimpanan Data Pribadi").  <br> <br>

        Personal Data will be stored by Do-It for at least 5 (five) years in accordance with article 15 paragraph (3) of the Minister of Communication and Information Technology Regulation Number 20 of 2016 concerning the Protection of Personal Data in Electronic Systems. Do-It will carry out this storage according to its policies or discretions, either through Do-It’s system, or other third-party’s system, server or database as a service provider within the territory of the Republic of Indonesia, designated by Do-It ("Appointment of the Third Party for Personal Data Storage"). <br> <br>

        c.Semua informasi dan data yang Pengguna berikan kepada Do-It akan disimpan dengan aman pada server di Negara Republik Indonesia. Semua jenis transaksi pembayaran juga akan dienkripsi menggunakan teknologi Secure Sockets Layer (SSL). Saat Do-It memberikan Pengguna (atau Pengguna telah memilih) sebuah kata sandi (password) yang memudahkan Pengguna untuk mengakses bagian tertentu dari Situs dan Aplikasi Do-it, maka Pengguna bertanggung jawab untuk memperlakukan sandi tersebut sebagai sesuatu yang sangat rahasia. Do-It menghimbau agar Pengguna tidak membagikan atau memberitahukan sandi tersebut kepada pihak yang tidak berkepentingan untuk mencegah adanya penggunaan yang tidak sah. Perlu diketahui, pengiriman informasi dan data melalui internet tidak sepenuhnya aman. Meskipun Do-It telah melakukan upaya yang terbaik untuk melindungi informasi dan data pribadi Pengguna, namun Do-It tidak dapat menjamin keamanan informasi dan data pribadi Pengguna yang dikirim melalui situs Do-It, dan karenanya pengiriman data dan/atau informasi apapun merupakan tanggung jawab Pengguna. <br>
    </p>
</div>

<div class="base_page_other">
    <p class="con_two">
        All information and data that the User provides to Do-It will be stored securely on the servers in the Republic of Indonesia. All types of payment transactions will also be encrypted using the Secure Sockets Layer (SSL) technology. When Do-It gives the User (or as chosen by the User) a password that enables the Users to easily access certain parts of the Website and Do-It’s Application, the User is responsible to safely keep the confidentiality of the password. Do-It advises that Users should not share or inform the password to any parties to prevent any unauthorized usage. Please note that sending information and data over the internet is not entirely secure. Despite Do-It’s best effort to protect User’s information and data, however Do-It cannot guarantee the safety of the User’s information and data sent through Do-It’s website, and therefore any transfer of data and/or information is the User’s sole responsibilities. <br><br>

        d.Apabila Pengguna mengetahui adanya penggunaan user id dan password, dan/atau akses yang tidak sah ke Akun Pengguna, maka Pengguna wajib untuk segera menginformasikannya kepada Do-It. Kegagalan untuk memberikan informasi tersebut merupakan tanggung jawab Pengguna sepenuhnya. <br><br>

        If the User awares of any usage of the user id and password, and/or any unauthorized access to the User’s Account, then the User must immediately inform Do-It. Failure to provide such notice shall be the User’s entire responsibilities.

        e.Ketika Do-It menerima informasi Pengguna, Do-It akan menggunakan prosedur yang ketat dan fitur keamanan sebagai usaha untuk mencegah akses yang tidak sah. Sistem keamanan Do-It telah memenuhi standar industri sesuai peraturan perundang-undangan yang berlaku, dan Do-It senantiasa mengamati perkembangan internet guna memastikan sistem Do-It berjalan dan berkembang seperti yang dipersyaratkan. Do-It juga melakukan tes terhadap sistem secara berkala untuk memastikan mekanisme keamanan Do-It selalu mutakhir dan sepenuhnya mematuhi peraturan perundang-undangan yang berlaku tentang perlindungan data di Indonesia.  <br><br>

        When Do-It receives the User’s information, Do-It will exercise strict procedures and security measures as an effort to prevent any unauthorized access. Do-It’s security system has met the industry standard according to the prevailing law and regulation, and Do-it constantly observes the internet development to ensure that Do-It’s system run and develop as required. Do-It also regularly tests the system to ensure that Do-It’s security mechanisms are always up-to-date and fully comply with the applicable laws and regulations in Indonesia <br>
    </p>
    <div class="t_qwe">
        D.Batasan dan Ganti Rugi <br>
        Limitation and Indemnification
    </div> <br><br>
    <p class="con_two">Do-It berkomitmen untuk tidak memperjualbelikan Data Pribadi Anda kepada pihak manapun, dan termasuk mengupayakan sewajarnya agar setiap karyawannya tidak terlibat dalam kegiatan jual beli Data Pribadi Anda kepada siapapun. Namun Do-It tidak bertanggung jawab atas kerugian dalam wujud apapun atau resiko kerugian yang Anda alami maupun pihak manapun yang terkait dengan Data Pribadi tersebut meliputi kegagalan atas perlindungan kerahasiaan Data Pribadi, penyerahan, akses, perolehan, pengolahan, penyimpanan, penggunaannya atau pengalihannya, termasuk sengketa, investigasi, audit, penegakan hukum dan proses penyelidikan apapun, yang terbukti tidak disebabkan oleh atau melibatkan Do-It maupun karyawan, perwakilan, kuasa, afiliasi atau pihak terkait Do-It lainnya ("Kerugian dan Masalah Hukum"). Adapun informasi atau data terkait Penerima Pinjaman yang dapat Kami berikan kepada Pemberi Pinjaman adalah sebatas data atau informasi yang diperbolehkan untuk diberi sesuai POJK dan Perjanjian Pinjam Meminjam. Selanjutnya, Pemberi Pinjaman juga menyadari dan memahami bahwa secara otomatis Do-It akan mengalihkan tugas penagihan (dimana di dalamnya terdapat data atau informasi Penerima Pinjaman) kepada Pemberi Pinjaman secara langsung atas pembayaran pinjaman yang sudah tertunggak ≥ 90 hari. <br><br>

        DO-It is committed not to trade User’s Personal Data to any parties, and this commitment includes conducting any reasonable efforts so that every employee will not be involved in any trade activities of User’s Personal Data to any parties. However, Do-It can not be held liable for any losses in any form or risk of loss that the User or any party suffered in relation to the said Personal Data including failure to protect the confidentiality of the Personal Data, submission, access, acquiring, processing, storage, usage or transfer, including dispustes, investigations, audits, law enforcement and any investigation process, which is proven not caused by or involving Do-It and its employees, representatives, attorney in facts, affiliates or other related parties of Do-It ("Loss and Legal Disputes"). The information or data related to the Borrower that Do-It may provide to the Lender is limited to data or information as permitted to be disclosed in accordance with POJK and the Loan Agreement. Furthermore, the Lender also </p>
</div>
<div class="base_page_other">
    <p>
        realizes and understands that Do-It will automatically transfer the collection duties (which contain data or information of the Borrower) directly to the Lender for any uncollectible debt aging ≥ 90 days. <br><br>

        Demi kejelasan, kepastian hukum dan transparansi: <br><br>

        For clarity, legal certainty and transparency <br>
    </p>
    <p class="con_two">
        a.Do-It tidak akan memberi ganti rugi atau merespon, dan tidak dapat dilibatkan (termasuk dalam hal ini karyawan, anak perusahaan, afiliasi atau perwakilannya) atas setiap klaim, tuntutan, permintaan atau pernyataan apapun yang timbul dari atau terkait Kerugian dan Masalah Hukum yang diajukan tanpa menyertakan perhitungan pasti yang telah dikuantifisir, akurat dan dapat dipertanggungjawabkan atas kerugian riil dan langsung (factual and direct damage) yang Pengguna alami tersebut, berikut bukti yang dapat diterima di pengadilan atau institusi yudisial mengenai kesalahan atau kelalaian Do-It berdasarkan Pasal 1865 dan 1866 Kitab Undang-Undang Hukum Perdata, Pasal 164 Het Herziene Indonesisch Reglement dan Pasal 5 ayat 1 Undang-Undang Nomor 11 Tahun 2008 tentang Informasi dan Transaksi Elektronik; <br> <br>

        Do-It shall not indemnify or respond for, and cannot be involved in (including its employees, subsidiaries, affiliates or representatives) any claims, lawsuits, requests or statements that arise from or related to losses and legal issues submitted without providing any definite calculations that have been quantified, accurate and accountable for any actual and direct damage suffered by the User, along with a valid evidence acceptable in court or judicial institutions regarding Do-It’s errors or negligence based on the Articles 1865 and 1866 of the Civil Law, Article 164 Het Herziene Indonesisch Reglement and Article 5 paragraph (1) Law Number 11 Year 2008 concerning the Information and Electronic Transactions. <br> <br>

        b.Do-It tidak bertanggung jawab atas akurasi (kecuali Data Pribadi yang telah terverifikasi oleh Do-It sesuai kebijakannya), keabsahan, legalitas dan kelengkapan Data Pribadi Pengguna, dan tidak diwajibkan untuk memberitahu Pengguna atau pihak manapun perihal tersebut kecuali diwajibkan secara hukum; dan/atau <br> <br>

        Do-It will not be responsible for the accuracy (except for the Personal Data that have been verified by Do-It at its sole discretions), validity, legality and completeness of the User’s Personal Data, and is not obligated to inform the User or any party regarding such matters unless as required by the Law; and/or <br> <br>

        c.Do-It tidak bertanggung jawab atas pelanggaran hukum yang Pengguna lakukan terhadap hak pihak ketiga manapun sebagai akibat dari perolehan atau pemberian Data Pribadi. Dalam hal terdapat Penunjukan Pihak Ketiga untuk Penyimpanan Data Pribadi, maka Pengguna tidak dapat menuntut, meminta ganti rugi atau manfaat apapun dari Do-It maupun karyawan, kuasa, perwakilan, afiliasi atau pihak terkait Do-It, bila terjadi pelanggaran kerahasiaan atau kegagalan perlindungan Data Pribadi, atau kerugian dan masalah hukum yang melibatkan, disebabkan atau dilakukan oleh Pihak Ketiga Penyimpan Data Pribadi. Namun demikian, Do-It tetap akan berupaya sewajarnya untuk memberitahu Pengguna perihal pelanggaran ketentuan kerahasiaan ketika Do-It mengetahuinya dari pihak ketiga tersebut. <br> <br>

        Do-It cannot be held liable for any violations commited by the User against any third party’s rights as a result from acquiring or providing Personal Data. Should there be any appointed Third Party for the Personal Data Storage, then the User cannot sue, request for any compensation or benefit from Do-it or its employees, representatives, affiliates or other related party of Do-It, if there is a breach of confidentiality provisions or failure to protect the Personal Data, or losses and legal issues which involve, caused by or conducted by the Third Party Personal Data Storage. However, Do-It shall, at its reasonable efforts, informs the User of such breach once notified by the said third party. <br> <br>
    </p>
    <div class="t_qwe">
        E.Hak Pengguna Untuk Merubah, Menambah atau Memperbaharui Data Pribadi <br>
        The User’s Rights to Revise, Add or Update Its Personal Data
    </div>
    <br>
    <p class="con_two">
        a.Untuk memastikan keterbaruan dan akurasi Data Pribadi Pengguna, Pengguna dapat mengajukan permohonan secara tertulis via e-mail kepada Do-It melalui: cs@do-it.id untuk tujuan perubahan, penambahan dan/atau pembaharuan Data Pribadi yang sebelumnya telah diperoleh atau diajukan kepada Do-It agar dapat ditindaklanjuti oleh Do-It sesuai diskresinya.  <br>
    </p>
</div>
<div class="base_page_other">
    <p class="con_two">
        To ensure the updated and accuracy of the User’s Personal Data, the User may submit a written request via e-mail to Do-It: cs@do-it.id for the purpose of revising, adding and/or updating the Personal Data that previously have been acquired or submitted to Do-It in order to be followed up by Do-It according to its own discretions.<br><br>

        b.Tanpa mengurangi ketentuan di atas, Pengguna diwajibkan untuk selalu memelihara dan memperbaharui data dan/atau informasi lainnya yang terkait dengan Pengguna. Untuk keperluan tersebut, Pengguna akan segera menginformasikan Do-It akan segala perubahan material pada data dan/atau informasi Pengguna yang dapat mempengaruhi kelayakan dan penggunaan Akun Pengguna.<br><br>

        Notwithstanding the foregoing, the User must at all times keep and update its data and/or other related information. For such purpose, the User will immediately informs Do-It of any material changes to the User’s data and/or information that can affect the feasibility and usage of the User’s Account.<br><br>
    </p>
    <div class="t_qwe">
        F.Keseluruhan Ketentuan <br>

        The Entire Provisions
    </div><br><br>
    <p class="con_two">
        Untuk menghindari adanya keraguan, Kebijakan Privasi beserta istilah-istilah di dalamnya haruslah digabungkan dengan Syarat dan Ketentuan Penggunaan Situs dan Aplikasi Do-it. Jika Pelanggan memutuskan untuk mendaftarkan diri sebagai Pengguna dalam Platform Do-It dan/atau melakukan pinjaman melalui Do-it, maka Kebijakan Privasi ini akan digabungkan juga dengan Syarat & Ketentuan Umum, Perjanjian Pinjam Meminjam dan/atau dokumen lainnya yang berhubungan dengan penggunaan Layanan Do-it. <br><br>

        To avoid any doubts, the Privacy Policy and all terms therein must be combined with the Terms and Conditions of the Usage of the Site and Do-it’s Application. If the User decides to register as a User in Do-It’s Platform and/or apply for a loan through Do-It, then this Privacy Policy will also be combined with the General Terms & Conditions, Loan Agreement and/or other documents related to the use of Do-It’s Services. <br><br>
    </p>

</div>

</body>
</html>