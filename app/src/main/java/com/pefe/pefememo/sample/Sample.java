package com.pefe.pefememo.sample;

import com.pefe.pefememo.model.directory.Directory;
import com.pefe.pefememo.model.memo.Memo;
import com.pefe.pefememo.model.todo.SelectedTodo;
import com.pefe.pefememo.model.todo.Todo;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by dodoproject on 2016-11-25.
 */

public class Sample {

    public static final String defaultCode = "false_dir_01";
    public static final String sampleCode = "true_dir_02";

    private static ArrayList<Memo> memos;
    private static ArrayList<Directory> directories;

    public static ArrayList<Directory> getDirectories(){
        directories = new ArrayList<>();
        Directory defaultDirectory;
        Directory sampleDirectory;

        sampleDirectory = new Directory();
        sampleDirectory.setNo(2);
        sampleDirectory.setOrder(2);
        sampleDirectory.setCode("true_dir_02");
        sampleDirectory.setName("first_Dir");
        sampleDirectory.setPw("1234");
        directories.add(sampleDirectory);

        defaultDirectory = new Directory();
        defaultDirectory.setNo(1);
        defaultDirectory.setOrder(1);
        defaultDirectory.setCode("false_dir_01");
        defaultDirectory.setName("default_Dir");
        defaultDirectory.setPw("");
        directories.add(defaultDirectory);

        return  directories;
    }
    public static ArrayList<Memo> getMemos(){
        memos = new ArrayList<>();
        Memo m1 = new Memo();
        m1.setNo(1);
        m1.setImportant(true);
        m1.setDirCode(defaultCode);
        m1.setContent("길은 지금 긴 산허리에 걸려 있다. 밤중을 지난 무렵인지 죽은 듯이 고요한 속에서 짐승 같은 달의 숨소리가 손에 잡힐 듯이 들리며, 콩 포기와 옥수수 잎새가 한층 달에 푸르게 젖었다. 산허리는 온통 메밀밭이어서 피기 시작한 꽃이 소금을 뿌린 듯이 흐붓한 달빛에 숨이 막힐 지경이다.");
        memos.add(m1);
        Memo m2 = new Memo();
        m2.setNo(2);
        m2.setImportant(false);
        m2.setDirCode(defaultCode);
        m2.setContent("...라는 구절로 유명한 이효석의 단편소설로 1936년에 발표되었으며 원래 이효석이 발표한 제목은 '모밀꽃 필 무렵'이다. 교과서에도 실렸으며 대한민국 사람이라면 누구나 읽었을 정도로 인지도 있는 작품이며 배경인 봉평(강원도 평창군)은 이 작품 덕에 먹고 산다는 말이 있을 정도이다.사실이다\n" +
                "\n" +
                "한국 문학사에 대단히 큰 한획을 그은 작품으로 이른바 소설에 주로 사용되는 서사 방식에서 상당량의 묘사를 사용하면서도 그 수준이 지극히 높은 수준이라 소설을 수필의 수준으로 끌어올렸다는 찬사를 얻는다. 바로 위의 구절만 봐도 알 수 있듯 아름답고 공감각적인 문체가 일품. 이야기 구성 면에서 꽤 재미있는 부분이 있는데, 허생원이 동이에 대해 마음을 열어갈수록 동이와 허생원의 물리적인 거리도 가까워진다. ");
        memos.add(m2);
        Memo m3 = new Memo();
        m3.setNo(3);
        m3.setImportant(false);
        m3.setDirCode(defaultCode);
        m3.setContent("교과서에도 자주 실렸고 한컴타자연습 때문에도 익숙하다. KBS 1TV의 구(舊) TV 문학관 시리즈와 2005년 이후의 HD 문학관 시리즈에서 드라마화하기도 했다. 1967년 이성구가 감독해 영화로도 제작됐는데 오래 전 작품이지만 소설을 좋아하면 구해서 볼 만하다. 젊은 시절의 이순재가 동이 역으로 나온다.");
        memos.add(m3);
        Memo m4 = new Memo();
        m4.setNo(4);
        m4.setImportant(false);
        m4.setDirCode(sampleCode);
        m4.setContent("김동화나 윤승운을 비롯한 만화가들도 한국 단편소설 원작 만화로 자주 그린 바 있다.\n");
        memos.add(m4);
        Memo m5 = new Memo();
        m5.setNo(5);
        m5.setImportant(true);
        m5.setDirCode(sampleCode);
        m5.setContent("허생원\n" +
                "소설의 주인공. 왼손잡이이고 얼금뱅이다. 한 평생 장돌뱅이로 살았으나 변변한 재산도 여자도 없는 늙은이로 아직도 장돌뱅이로 떠돈다. 젊은 시절에는 돈을 조금 벌기도 했지만 노름으로 죄다 날리고 가족이라곤 늙은 나귀 한 마리뿐이라 이 나귀를 극진히 아낀다. 이 나귀도 노름으로 팔아치우려다가 그만두고 후일 노름빚 때문에 도망할 때 나귀를 데리고 가면서 너를 안 팔아 다행이라고 울기까지 했다고 한다.\n" +
                "그러던 어느날, 우연히 만난 젊은 장돌뱅이 동이와 동행하면서 서로 투닥투닥하는 사이지만 나중에 가면 자기가 젊었을 때 물레방앗간에서 있었던 하룻밤 썰 팔자에 있었나부지. 성처녀와의 이야기와 동이가 해 준 자기 어머니의 이야기가 비슷하다는 것, 그리고 둘 다 같은 왼손잡이라는 것[1]발가락이 닮았다을 보고 동이가 자기 아들이라고 확신하면서 동이의 어머니가 있다는 제천장으로 동이와 함께 간다. 동이가 허생원의 아들이라는 것과 허생원과 성씨가 재회하리라고 암시하면서 소설은 끝난다. 연명판 애니에서의 성우는 기영도.");
        memos.add(m5);
        Memo m6 = new Memo();
        m6.setNo(6);
        m6.setImportant(true);
        m6.setDirCode(sampleCode);
        m6.setContent("조선달\n" +
                "허생원의 친구로 나이가 지긋한 장돌뱅이. 허생원, 동이와 장을 함께 다니면서 그 사람들의 옆에 있어 준다. 연명판 애니에서의 성우는 이인성.\n" +
                "동이 \n" +
                "젊은 장돌뱅이. 아버지 얼굴을 모르고 태어나 계부와 마찰을 계기로 장돌뱅이가 되었다. 여담이지만 왼손잡이가 유전되는지는 논란이 있지만, 두 사람의 부자 관계를 암시하는 문학적 장치라 봐야 한다. I Am Your Father[2][3] 연명판 애니에서의 성우는 엄상현.");
        memos.add(m6);
    return memos;
    }

    public static ArrayList<Todo> createSampleTodo(){
        ArrayList<Todo> sample = new ArrayList<>();
        Todo todo1 = new Todo();
        todo1.setNo(1);
        todo1.setContent("밥 먹기");
        todo1.setCreateDate(Calendar.getInstance().getTime());
        todo1.setType(Todo.REPEAT);
        sample.add(todo1);

        Todo todo2 = new Todo();
        todo2.setNo(2);
        todo2.setContent("잠자기");
        todo2.setCreateDate(Calendar.getInstance().getTime());
        todo2.setType(Todo.REPEAT);
        sample.add(todo2);

        Todo todo3 = new Todo();
        todo3.setNo(3);
        todo3.setContent("Realm");
        todo3.setCreateDate(Calendar.getInstance().getTime());
        todo3.setType(Todo.ONCE);
        sample.add(todo3);

        return sample;
    }

    public static ArrayList<SelectedTodo> createSampleSelectedTodo(){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE,-3);
        ArrayList<SelectedTodo> sample = new ArrayList<>();
        SelectedTodo sTodo1 = new SelectedTodo();
        sTodo1.setDone(false);
        sTodo1.setType(Todo.ONCE);
        sTodo1.setContent("계좌 이체");
        sTodo1.setBelongDate(cal.getTime());
        sample.add(sTodo1);
        SelectedTodo sTodo2 = new SelectedTodo();
        sTodo2.setDone(false);
        sTodo2.setType(Todo.REPEAT);
        sTodo2.setContent("잠자기");
        sTodo2.setBelongDate(cal.getTime());
        sample.add(sTodo2);
        SelectedTodo sTodo3 = new SelectedTodo();
        sTodo3.setDone(true);
        sTodo3.setType(Todo.REPEAT);
        sTodo3.setContent("잠자기");
        sTodo3.setBelongDate(cal.getTime());
        cal.add(Calendar.DATE,6);
        SelectedTodo sTodo4 = new SelectedTodo();
        sTodo4.setDone(false);
        sTodo4.setType(Todo.REPEAT);
        sTodo4.setContent("잠자기");
        sTodo4.setBelongDate(cal.getTime());
        sample.add(sTodo4);
        return sample;
    }

}
