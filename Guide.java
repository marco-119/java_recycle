package recycle;

import recycle.db.recycleDB;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class PaperPanel extends JPanel{
	public PaperPanel() {
		setLayout(new BorderLayout());
        String htmlContent = "<html><head><style>" 
                + "h2{ "                    // 제목
                + "	 margin-top: 10px; "   // 제목 위쪽 여백 10px
                + "	 margin-bottom: 15px; " // h2 아래쪽 여백 15px
                + "}"
                
                + "h3 { " 
                + "	 margin-top: 10px; "   // 제목 위쪽 여백 10px
                + "	 margin-bottom: 5px; " // 제목 아래쪽 여백 5px
                + "}"
        		
				+ "ul { "
				+ "  margin-top: 5px; "    // 목록 위쪽 여백 5px
				+ "  margin-left: 20px; "  // 목록 왼쪽 여백 20px (들여쓰기)
				+ "}"
                
                + "li {"
                + "  margin-bottom: 5px; " // 각 항목 아래쪽 여백 5px
                + "}"
                
                + ".note { "
                + "  padding-left: 15px; "    // 왼쪽 여백 15px
                + "  list-style-type: none; " // 목록 앞에 붙는 점(•)을 없앰
                + "}"
                + "</style></head><body>"
                
                + "<h2>종이류 (고지류)</h2>"
                + "<h3>세부품목</h3>"
                + "<li>신문지 등</li>"
                + "<h3>배출방법</h3>"
                + "<ul>"
                + "<li>물기에 젖지 않도록 하고, 반듯하게 펴서 차곡차곡 쌓은 후 흩날리지 않도록 끈 등으로 묶어서 배출</li>"
                + "</ul>"
                + "<h3>비해당품목</h3>"
                + "<ul>"
                + "<li>비닐 코팅 종이(광고지, 치킨 속포장재 등), 금박 은박지, 벽지, 자석전단지, 이물질을 제거하기 어려운 경우 등</li>"
                + "<li class=\"note\">* 종량제봉투로 배출</li>"
                + "</ul>"
                + "<hr>"
                
                + "<h3>세부품목</h3>"
                + "<li>책자, 노트 등</li>"
                + "<h3>배출방법</h3>"
                + "<ul>"
                + "<li>스프링 등 종이류와 다른 재질은 제거한 후 배출</li>"
                + "</ul>"
                + "<h3>해당품목</h3>"
                + "<ul>"
                + "<li>책, 잡지, 공책 노트 등</li>"
                + "</ul>"
                + "<h3>비해당품목</h3>"
                + "<ul>"
                + "<li>비닐 코팅된 표지, 공책의 스프링 등</li>"
                + "<li class=\"note\">* 부속 재질에 따라 분리배출하거나 종량제봉투 등으로 배출</li>"
                + "</ul>"
                + "<hr>"
                
				+ "<h3>세부품목</h3>"
				+ "<li>상자류</li>"
                + "<h3>배출방법</h3>"
                + "<ul>"
                + "<li>테이프 등 종이류와 다른 재질은 제거한 후 배출</li>"
                + "</ul>"
                + "<h3>해당품목</h3>"
                + "<ul>"
                + "<li>종이박스, 골판지 등</li>"
                + "</ul>"
                + "<h3>비해당품목</h3>"
                + "<ul>"
                + "<li>비닐코팅 부분, 상자에 붙어있는 테이프, 철핀 등</li>"
                + "<li class=\"note\">* 부속 재질에 따라 분리배출하거나 종량제봉투 등으로 배출</li>"
                + "</ul>"
                
                + "</body></html>";

        JEditorPane editorPane = new JEditorPane(); // 텍스트 상자 생성
        editorPane.setContentType("text/html"); // 텍스트 상자 HTML 형식 지정
        editorPane.setText(htmlContent); // 분리수거 방법 설명 넣기
        editorPane.setEditable(false); // 사용자가 수정 못하게 읽기 전용으로 설정
        JScrollPane scrollPane = new JScrollPane(editorPane); // 스크롤 기능 추가
        add(scrollPane, BorderLayout.CENTER); 
    }
}

class VinylPanel extends JPanel{
	public VinylPanel() {
		setLayout(new BorderLayout());
        String htmlContent = "<html><head><style>"
        		+ "h2{ "                    // 제목
                + "	 margin-top: 10px; "   // 제목 위쪽 여백 10px
                + "	 margin-bottom: 15px; " // h2 아래쪽 여백 15px
                + "}"
                
                + "h3 { " 
                + "	 margin-top: 10px; "   // 제목 위쪽 여백 10px
                + "	 margin-bottom: 5px; " // 제목 아래쪽 여백 5px
                + "}"
                
				+ "ul { "
				+ "  margin-top: 5px; "    // 목록 위쪽 여백 5px
				+ "  margin-left: 20px; "  // 목록 왼쪽 여백 20px (들여쓰기)
				+ "}"
                
                + "li {"
                + "  margin-bottom: 5px; " // 각 항목 아래쪽 여백 5px
                + "}"
                
                + ".note { "
                + "  padding-left: 15px; "    // 왼쪽 여백 15px
                + "  list-style-type: none; " // 목록 앞에 붙는 점(•)을 없앰
                + "}"
                + "</style></head><body>"
                
                + "<h2>합성수지류</h2>"
                + "<h3>세부품목</h3>"
                + "<li>비닐포장재, 1회용 비닐봉투 </li>"
                + "<h3>배출방법</h3>"
                + "<ul>"
                + "<li>내용물을 비우고 물로 헹구는 등 이물질을 제거하여 배출</li>"
                + "<li>흩날리지 않도록 봉투에 담아 배출</li>"
                + "</ul>"
                + "<h3>해당품목</h3>"
                + "<ul>"
                + "<li>1회용 봉투 등 각종 비닐류</li>"
                + "<li class=\"note\">* 분리배출표시가 없는 비닐류 포함</li>"
                + "</ul>"
                + "<h3>비해당품목</h3>"
                + "<ul>"
                + "<li>깨끗하게 이물질 제거가 되지 않은 랩필름 등</li>"
                + "<li>식탁보, 고무장갑, 장판, 돗자리, 섬유류 등(천막, 현수막, 의류, 침구류 등)</li>"
                + "<li class=\"note\">* 종량제봉투, 특수규격마대 또는 대형폐기물 처리 등 지자체 조례에 따라 배출</li>"
                + "</ul>"
                
                + "</body></html>";

        JEditorPane editorPane = new JEditorPane(); // 텍스트 상자 생성
        editorPane.setContentType("text/html"); // 텍스트 상자 HTML 형식 지정
        editorPane.setText(htmlContent); // 분리수거 방법 설명 넣기
        editorPane.setEditable(false); // 사용자가 수정 못하게 읽기 전용으로 설정
        JScrollPane scrollPane = new JScrollPane(editorPane); // 스크롤 기능 추가
        add(scrollPane, BorderLayout.CENTER); 
	}
}

class GlassBottlePanel extends JPanel{
	public GlassBottlePanel() {
		setLayout(new BorderLayout());
        String htmlContent = "<html><head><style>"
        		+ "h2{ "                    // 제목
                + "	 margin-top: 10px; "   // 제목 위쪽 여백 10px
                + "	 margin-bottom: 15px; " // h2 아래쪽 여백 15px
                + "}"
                
                + "h3 { " 
                + "	 margin-top: 10px; "   // 제목 위쪽 여백 10px
                + "	 margin-bottom: 5px; " // 제목 아래쪽 여백 5px
                + "}"
        		
				+ "ul { "
				+ "  margin-top: 5px; "    // 목록 위쪽 여백 5px
				+ "  margin-left: 20px; "  // 목록 왼쪽 여백 20px (들여쓰기)
				+ "}"
                
                + "li {"
                + "  margin-bottom: 5px; " // 각 항목 아래쪽 여백 5px
                + "}"
                
                + ".note { "
                + "  padding-left: 15px; "    // 왼쪽 여백 15px
                + "  list-style-type: none; " // 목록 앞에 붙는 점(•)을 없앰
                + "}"
                + "</style></head><body>"
                
                + "<h2>유리병</h2>"
                + "<h3>세부품목</h3>"
                + "<li>음료수병, 기타병류</li>"
                + "<h3>배출방법</h3>"
                + "<ul>"
                + "<li>내용물을 비우고 물로 헹구는 등 이물질을 제거하여 배출</li>"
                + "<li>담배꽁초 등 이물질을 넣지 않고 배출</li>"
                + "<li>유리병이 깨지지 않도록 주의하여 배출</li>"
                + "<li>소주, 맥주 등 빈용기보증금 대상 유리병은 소매점 등으로 반납하여 보증금 환급</li>"
                + "</ul>"
                + "<h3>해당품목</h3>"
                + "<ul>"
                + "<li>음료수병, 와인병, 양주병, 드링크병 등</li>"
                + "</ul>"
                + "<h3>비해당품목</h3>"
                + "<ul>"
                + "<li>깨진 유리제품은 신문지 등에 싸서 종량제 봉투 배출</li>"
                + "<li>코팅 및 다양한 색상이 들어간 유리제품, 내열 유리제품, 크리스탈 유리제품, 판유리, 조명기구용 유리류, 사기·도자기류 등</li>"
                + "<li class=\"note\">* 특수규격마대 또는 대형폐기물 처리 등 지자체 조례에 따라 배출</li>"
                + "</ul>"
                
                + "</body></html>";

        JEditorPane editorPane = new JEditorPane(); // 텍스트 상자 생성
        editorPane.setContentType("text/html"); // 텍스트 상자 HTML 형식 지정
        editorPane.setText(htmlContent); // 분리수거 방법 설명 넣기
        editorPane.setEditable(false); // 사용자가 수정 못하게 읽기 전용으로 설정
        JScrollPane scrollPane = new JScrollPane(editorPane); // 스크롤 기능 추가
        add(scrollPane, BorderLayout.CENTER); 
	}
}

class PaperPackPanel extends JPanel {
    public PaperPackPanel() {
    	setLayout(new BorderLayout());
        String htmlContent = "<html><head><style>"
        		+ "h2{ "                    // 제목
                + "	 margin-top: 10px; "   // 제목 위쪽 여백 10px
                + "	 margin-bottom: 15px; " // h2 아래쪽 여백 15px
                + "}"
                
                + "h3 { " 
                + "	 margin-top: 10px; "   // 제목 위쪽 여백 10px
                + "	 margin-bottom: 5px; " // 제목 아래쪽 여백 5px
                + "}"
        		
				+ "ul { "
				+ "  margin-top: 5px; "    // 목록 위쪽 여백 5px
				+ "  margin-left: 20px; "  // 목록 왼쪽 여백 20px (들여쓰기)
				+ "}"
                
                + "li {"
                + "  margin-bottom: 5px; " // 각 항목 아래쪽 여백 5px
                + "}"
                
                + ".note { "
                + "  padding-left: 15px; "    // 왼쪽 여백 15px
                + "  list-style-type: none; " // 목록 앞에 붙는 점(•)을 없앰
                + "}"
                + "</style></head><body>"
                
                + "<h2>종이팩</h2>"
                + "<h3>세부품목</h3>"
                + "<li>살균팩, 멸균팩</li>"
                + "<h3>배출방법</h3>"
                + "<ul>"
                + "<li>내용물을 비우고 물로 헹구는 등 이물질을 제거하고 말린 후 배출</li>"
                + "<li>빨대, 비닐 등 종이팩과 다른 재질은 제거한 후 배출</li>"
                + "<li>일반 종이류와 혼합되지 않게 종이팩 전용수거함에 배출</li>"
                + "<li>종이팩 전용수거함이 없는 경우에는 종이류와 구분할 수 있도록 가급적 끈 등으로 묶어 종이류 수거함으로 배출</li>"
                + "</ul>"
                + "<h3>해당품목</h3>"
                + "<ul>"
                + "<li>우유팩, 두유팩 소주팩, 쥬스팩 등</li>"
                + "</ul>"
                + "<h3>비해당품목</h3>"
                + "<ul>"
                + "<li>종이, 신문지 등 종이류, 종이컵 등</li>"
                + "<li class=\"note\">* 종이류 수거함으로 배출</li>"
                + "</ul>"
                + "<hr>"
                
                + "<h2>종이컵</h2>"
                + "<h3>배출방법</h3>"
                + "<ul>"
                + "<li>내용물을 비우고 물로 헹구는 등 이물질을 제거하여 배출</li>"
                + "</ul>"
                
                + "</body></html>";

        JEditorPane editorPane = new JEditorPane(); // 텍스트 상자 생성
        editorPane.setContentType("text/html"); // 텍스트 상자 HTML 형식 지정
        editorPane.setText(htmlContent); // 분리수거 방법 설명 넣기
        editorPane.setEditable(false); // 사용자가 수정 못하게 읽기 전용으로 설정
        JScrollPane scrollPane = new JScrollPane(editorPane); // 스크롤 기능 추가
        add(scrollPane, BorderLayout.CENTER); 
    }
}

class MetalPanel extends JPanel{
	public MetalPanel() {
		setLayout(new BorderLayout());
        String htmlContent = "<html><head><style>"
        		+ "h2{ "                    // 제목
                + "	 margin-top: 10px; "    // 제목 위쪽 여백 10px
                + "	 margin-bottom: 15px; " // h2 아래쪽 여백 15px
                + "}"
                
                + "h3 { " 
                + "	 margin-top: 10px; "   // 제목 위쪽 여백 10px
                + "	 margin-bottom: 5px; " // 제목 아래쪽 여백 5px
                + "}"
        		
				+ "ul { "
				+ "  margin-top: 5px; "    // 목록 위쪽 여백 5px
				+ "  margin-left: 20px; "  // 목록 왼쪽 여백 20px (들여쓰기)
				+ "}"
                
                + "li {"
                + "  margin-bottom: 5px; " // 각 항목 아래쪽 여백 5px
                + "}"
                
                + ".note { "
                + "  padding-left: 15px; "    // 왼쪽 여백 15px
                + "  list-style-type: none; " // 목록 앞에 붙는 점(•)을 없앰
                + "}"
                + "</style></head><body>"
                
                + "<h2>금속캔</h2>"
                + "<h3>세부품목</h3>"
                + "<li>음료·주류캔, 식료품캔</li>"
                + "<h3>배출방법</h3>"
                + "<ul>"
                + "<li>내용물을 비우고 물로 헹구는 등 이물질을 제거하여 배출</li>"
                + "<li>담배꽁초 등 이물질을 넣지 않고 배출</li>"
                + "<li>플라스틱 뚜껑 등 금속캔과 다른 재질은 제거한 후 배출</li>"
                + "</ul>"
                + "<h3>해당품목</h3>"
                + "<ul>"
                + "<li>음료수캔, 맥주캔, 통조림캔 등</li>"
                + "</ul>"
                + "<h3>비해당품목</h3>"
                + "<ul>"
                + "<li>알루미늄 호일 등</li>"
                + "<li class=\"note\">* 종량제 봉투로 배출</li>"
                + "</ul>"
                + "<hr>"
                
                + "<h2>기타캔류 (부탄가스, 살충제용기 등)</h2>"
                + "<h3>배출방법</h3>"
                + "<ul>"
                + "<li>내용물을 제거한 후 배출</li>"
                + "<li class=\"note\">* 가스용기는 가급적 통풍이 잘되는 장소에서 노즐을 누르는 등 내용물을 완전히 제거한 후 배출</li>"
                + "</ul>"
                + "<h3>해당품목</h3>"
                + "<ul>"
                + "<li>부탄가스 용기, 살충제 용기, 스프레이 용기 등</li>"
                + "</ul>"
                + "<h3>비해당품목</h3>"
                + "<ul>"
                + "<li class=\"note\">* 내용물이 남아있는 캔류(락카, 페인트통 등)는 특수규격마대 등 지자체 조례에 따라 배출</li>"
                + "</ul>"
                + "<br><hr style=\"border: 1px dashed #999;\"><br>"
                
                + "<h2>고철류</h2>"
                + "<h3>세부품목</h3>"
                + "<li>고철</li>"
                + "<h3>배출방법</h3>"
                + "<ul>"
                + "<li>이물질이 섞이지 않도록 한 후 배출</li>"
                + "</ul>"
                + "<h3>해당품목</h3>"
                + "<ul>"
                + "<li>공기구, 철사, 못 등 고철류, 알루미늄, 스테인레스 제품 등 비철금속류</li>"
                + "</ul>"
                + "<h3>비해당품목</h3>"
                + "<ul>"
                + "<li>금속 이외의 재질(천, 고무, 플라스틱 등)이 부착된 우산, 프라이팬, 전기용품 등</li>"
                + "<li class=\"note\">* 재질별로 분리가 곤란한 경우에는 종량제봉투, 특수규격마대 또는 대형폐기물 처리 등 지자체 조례에 따라 배출</li>"
                + "</ul>"
                
                + "</body></html>";

        JEditorPane editorPane = new JEditorPane(); // 텍스트 상자 생성
        editorPane.setContentType("text/html"); // 텍스트 상자 HTML 형식 지정
        editorPane.setText(htmlContent); // 분리수거 방법 설명 넣기
        editorPane.setEditable(false); // 사용자가 수정 못하게 읽기 전용으로 설정
        JScrollPane scrollPane = new JScrollPane(editorPane); // 스크롤 기능 추가
        add(scrollPane, BorderLayout.CENTER); 
	}
}

class StyrofoamPanel extends JPanel{
	public StyrofoamPanel() {
		setLayout(new BorderLayout());
        String htmlContent = "<html><head><style>"
        		+ "h2{ "                    // 제목
                + "	 margin-top: 10px; "   // 제목 위쪽 여백 10px
                + "	 margin-bottom: 15px; " // h2 아래쪽 여백 15px
                + "}"
                
                + "h3 { " 
                + "	 margin-top: 10px; "   // 제목 위쪽 여백 10px
                + "	 margin-bottom: 5px; " // 제목 아래쪽 여백 5px
                + "}"
        		
				+ "ul { "
				+ "  margin-top: 5px; "    // 목록 위쪽 여백 5px
				+ "  margin-left: 20px; "  // 목록 왼쪽 여백 20px (들여쓰기)
				+ "}"
                
                + "li {"
                + "  margin-bottom: 5px; " // 각 항목 아래쪽 여백 5px
                + "}"
                
                + ".note { "
                + "  padding-left: 15px; "    // 왼쪽 여백 15px
                + "  list-style-type: none; " // 목록 앞에 붙는 점(•)을 없앰
                + "}"
                + "</style></head><body>"
                
                + "<h2>합성수지류</h2>"
                + "<h3>세부품목</h3>"
                + "<li>스티로폼 완충재</li>"
                + "<h3>배출방법</h3>"
                + "<ul>"
                + "<li>내용물을 비우고 물로 헹구는 등 이물질을 제거하여 배출</li>"
                + "<li>부착상표 등 스티로폼과 다른 재질은 제거한 후 배출</li>"
                + "<li>TV 등 전자제품 구입 시 완충재로 사용되는 발포합성수지 포장재는 가급적 구입처로 반납</li>"
                + "</ul>"
                + "<h3>해당품목</h3>"
                + "<ul>"
                + "<li>농·수·축산물 포장용 발포스티렌상자, 전자제품 완충재로 사용되는 발포합성수지포장재</li>"
                + "</ul>"
                + "<h3>비해당품목</h3>"
                + "<ul>"
                + "<li>타 재질과 코팅 또는 접착된 발포스티렌, 건축용 내외장재 스티로폼, 이물질을 제거하기 어려운 경우 등</li>"
                + "<li class=\"note\">* 종량제봉투, 특수규격마대 또는 대형폐기물 처리 등 지자체 조례에 따라 배출</li>"
                + "</ul>"
                
                + "</body></html>";

        JEditorPane editorPane = new JEditorPane(); // 텍스트 상자 생성
        editorPane.setContentType("text/html"); // 텍스트 상자 HTML 형식 지정
        editorPane.setText(htmlContent); // 분리수거 방법 설명 넣기
        editorPane.setEditable(false); // 사용자가 수정 못하게 읽기 전용으로 설정
        JScrollPane scrollPane = new JScrollPane(editorPane); // 스크롤 기능 추가
        add(scrollPane, BorderLayout.CENTER); 
	}
}

class PlasticPanel extends JPanel{
	public PlasticPanel() {
		setLayout(new BorderLayout());
        String htmlContent = "<html><head><style>"
        		+ "h2{ "                    // 제목
                + "	 margin-top: 10px; "   // 제목 위쪽 여백 10px
                + "	 margin-bottom: 15px; " // h2 아래쪽 여백 15px
                + "}"
                
                + "h3 { " 
                + "	 margin-top: 10px; "   // 제목 위쪽 여백 10px
                + "	 margin-bottom: 5px; " // 제목 아래쪽 여백 5px
                + "}"
        		
				+ "ul { "
				+ "  margin-top: 5px; "    // 목록 위쪽 여백 5px
				+ "  margin-left: 20px; "  // 목록 왼쪽 여백 20px (들여쓰기)
				+ "}"
                
                + "li {"
                + "  margin-bottom: 5px; " // 각 항목 아래쪽 여백 5px
                + "}"
                
                + ".note { "
                + "  padding-left: 15px; "    // 왼쪽 여백 15px
                + "  list-style-type: none; " // 목록 앞에 붙는 점(•)을 없앰
                + "}"
                + "</style></head><body>"
                
                + "<h2>합성수지류</h2>"
                + "<h3>세부품목</h3>"
                + "<li>PET, PVC, PE, PP, PS, PSP 재질 등의 용기·트레이류</li>"
                + "<h3>배출방법</h3>"
                + "<ul>"
                + "<li>내용물을 비우고 물로 헹구는 등 이물질을 제거하여 배출</li>"
                + "<li class=\"note\">* 물로 헹굴 수 없는 구조의 용기류(치약용기 등)는 내용물을 비운 후 배출</li>"
                + "<li>부착상표, 부속품 등 본체와 다른 재질은 제거한 후 배출</li>"
                + "</ul>"
                + "<h3>해당품목</h3>"
                + "<ul>"
                + "<li>음료용기, 세정용기 등</li>"
                + "</ul>"
                + "<h3>비해당품목</h3>"
                + "<ul>"
                + "<li>플라스틱 이외의 재질이 부착된 완구 문구류, 옷걸이, 칫솔, 파일철, 전화기, 낚싯대, 유모차 보행기, CD DVD 등</li>"
                + "<li class=\"note\">* 종량제봉투, 특수규격마대 또는 대형폐기물 처리 등 지자체 조례에 따라 배출</li>"
                + "</ul>"
                
                + "</body></html>";

        JEditorPane editorPane = new JEditorPane(); // 텍스트 상자 생성
        editorPane.setContentType("text/html"); // 텍스트 상자 HTML 형식 지정
        editorPane.setText(htmlContent); // 분리수거 방법 설명 넣기
        editorPane.setEditable(false); // 사용자가 수정 못하게 읽기 전용으로 설정
        JScrollPane scrollPane = new JScrollPane(editorPane); // 스크롤 기능 추가
        add(scrollPane, BorderLayout.CENTER); 
	}
}

class EtcPanel extends JPanel{
	public EtcPanel() {
		setLayout(new BorderLayout());
        String htmlContent = "<html><head><style>"
        		+ "h2{ "                    // 제목
                + "	 margin-top: 10px; "   // 제목 위쪽 여백 10px
                + "	 margin-bottom: 15px; " // h2 아래쪽 여백 15px
                + "}"
                
                + "h3 { " 
                + "	 margin-top: 10px; "   // 제목 위쪽 여백 10px
                + "	 margin-bottom: 5px; " // 제목 아래쪽 여백 5px
                + "}"
        		
				+ "ul { "
				+ "  margin-top: 5px; "    // 목록 위쪽 여백 5px
				+ "  margin-left: 20px; "  // 목록 왼쪽 여백 20px (들여쓰기)
				+ "}"
                
                + "li {"
                + "  margin-bottom: 5px; " // 각 항목 아래쪽 여백 5px
                + "}"
                
                + ".note { "
                + "  padding-left: 15px; "    // 왼쪽 여백 15px
                + "  list-style-type: none; " // 목록 앞에 붙는 점(•)을 없앰
                + "}"
                + "</style></head><body>"
                
                + "<h2>의류 및 원단류</h2>"
                + "<h3>세부품목</h3>"
                + "<li>면의류, 기타 의류, 동·식물성 섬유, 합성섬유류</li>"
                + "<h3>배출방법</h3>"
                + "<ul>"
                + "<li>지자체 또는 민간 재활용사업자가 비치한 폐의류 전용수거함에 배출</li>"
                + "<li>전용수거함이 없는 문전수거 지역 등에서는 물기에 젖지 않도록 마대 등에 담거나 묶어서 배출</li>"
                + "</ul>"
                
                + "</body></html>";

        JEditorPane editorPane = new JEditorPane(); // 텍스트 상자 생성
        editorPane.setContentType("text/html"); // 텍스트 상자 HTML 형식 지정
        editorPane.setText(htmlContent); // 분리수거 방법 설명 넣기
        editorPane.setEditable(false); // 사용자가 수정 못하게 읽기 전용으로 설정
        JScrollPane scrollPane = new JScrollPane(editorPane); // 스크롤 기능 추가
        add(scrollPane, BorderLayout.CENTER); 
	}
}


class JTabbedPane1 extends JFrame {
	public JTabbedPane1() {
		JTabbedPane jtpLeft = new JTabbedPane();
		jtpLeft.setTabPlacement(JTabbedPane.LEFT);
		
		PaperPanel paper = new PaperPanel();
		VinylPanel vinyl = new VinylPanel();
		GlassBottlePanel glassBottle = new GlassBottlePanel();
		PaperPackPanel paperPack = new PaperPackPanel();
		MetalPanel metal = new MetalPanel();
		StyrofoamPanel styrofoam = new StyrofoamPanel();
		PlasticPanel plastic = new PlasticPanel();
		EtcPanel etc = new EtcPanel();
		
		jtpLeft.addTab("종이", paper);
		jtpLeft.addTab("비닐", vinyl);
		jtpLeft.addTab("유리병", glassBottle);
		jtpLeft.addTab("종이팩", paperPack);
		jtpLeft.addTab("캔ㆍ고철", metal);
		jtpLeft.addTab("스티로폼", styrofoam);
		jtpLeft.addTab("플라스틱", plastic);
		jtpLeft.addTab("기타", etc);
		
		getContentPane().add(jtpLeft);
		
		setTitle("JTabbedPaneTest1");
		setSize(500,300);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}

public class Guide {
	public static void main(String[] args) {		
		new JTabbedPane1();
	}
}