package com.pefe.pefememo.app.fragments.todo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.pefe.pefememo.R;
import com.pefe.pefememo.model.todo.SelectedTodo;
import com.pefe.pefememo.model.todo.Todo;
import com.pefe.pefememo.realm.RealmController;
import com.pefe.pefememo.realm.RealmControllerImpl;
import com.pefe.pefememo.sample.Sample;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import io.realm.OrderedRealmCollection;


public class TodoFragment extends Fragment implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener, TodoHandler {

    RealmController realmController;
    public TodoFragment() {
        // Required empty public constructor
    }


    public static TodoFragment newInstance() {
        TodoFragment fragment = new TodoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /*
        -------------------------laout component-----------------------------
        TextView tv_goal : 목표나 좌우명
                 tv_yearMonth : 달력의 년도와 달을 표현하는 부분
                 tv__nDate : 달력의 일자와 요일을 표현하는 부분
        ImageButton ib_before : 달력의 날짜 하루 전으로 이동
                    ib_next : 달력의 날짜 하루 후로 이동
        RecyclerView unRegister_todoList : TodoMemo가 어느 날짜에도 속하지 않은 상태의 리스트뷰
                     registered_todoList : TodoMemo가 어느 날짜에 속했을 상태의 리스트 뷰
        Spinner sp_todoType : 등록되지 않은 리스트 뷰를 Once,Repeat,Old 타입별로 분류하기 위한 Spinner
        LinearLayout menu_layout : 목표와 달력의 년월이 들어있는 레이아웃이다.
                     delete_layout : 삭제버튼이 들어있는 레이아웃으로 드래그 시에 보이면 메뉴 레이아웃이
                                     안보여진다.
     */
    private TextView tv_goal, tv_yearMonth, tv_nDate;
    private ImageButton ib_before, ib_next;
    private RecyclerView unRegister_todoList, registered_todoList;
    private Spinner sp_todoType;
    private LinearLayout delete_layout, menu_layout;

    //goal dialog 창에 관련된 것들
    private LayoutInflater inflater;
    private ViewGroup parent;
    private View dialogView;
    private View oldTodoView;


    /*
        today : 오늘 날짜
        selected_day : 선택된 날짜
        cal : calendar 변수
     */
    private Date today, selcted_day;
    private Calendar cal;


    private TodoDragListener dragListener;
    private TodoTypeSelectItemListener selectItemListener;

    private UnRegisterAdapter onceAdapter, repeatAdapter, oldAdapter;
    private HashMap<String, UnRegisterAdapter> map_unRegister_adapters;
    private static final String ONCE = Todo.ONCE;
    private static final String REPEAT = Todo.REPEAT;
    private static final String OLD = Todo.OLD;

    private HashMap<Date, RegisteredAdapter> map_register_adapters;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        realmController = new RealmControllerImpl(getContext());
        realmController.realmInit();
        View view = inflater.inflate(R.layout.fragment_todo, container, false);
        this.inflater = inflater;
        parent = container;
        init(view);
        return view;
    }

    //초기화하는 함수입니다.
    private void init(View view) {
        tv_goal = (TextView) view.findViewById(R.id.tv_goal);
        tv_nDate = (TextView) view.findViewById(R.id.tv_nDate);
        tv_yearMonth = (TextView) view.findViewById(R.id.tv_yearMonth);
        ib_before = (ImageButton) view.findViewById(R.id.ib_before);
        ib_next = (ImageButton) view.findViewById(R.id.ib_next);
        unRegister_todoList = (RecyclerView) view.findViewById(R.id.unRegister_todoList);
        registered_todoList = (RecyclerView) view.findViewById(R.id.registered_todoList);
        sp_todoType = (Spinner) view.findViewById(R.id.sp_todoType);
        delete_layout = (LinearLayout) view.findViewById(R.id.delete_layout);
        menu_layout = (LinearLayout) view.findViewById(R.id.menu_layout);

        map_register_adapters = new HashMap<>();
        map_unRegister_adapters = new HashMap<>();

        init_goal();

        init_dateData();
        init_listview();
        init_listener();
        init_bottomAdapter();
        init_todoData();
        init_topAdapter();
        show_selected_oldTodo();


    }

    private void init_listview() {
        RecyclerView.LayoutManager manager1 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        TodoViewType viewType1 = new TodoViewType();
        viewType1.setType("Bottom");
        viewType1.setIndex(0);
        unRegister_todoList.setTag(viewType1);
        unRegister_todoList.setLayoutManager(manager1);
        RecyclerView.LayoutManager manager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        TodoViewType viewType2 = new TodoViewType();
        viewType2.setType("Today_list");
        viewType2.setIndex(0);
        registered_todoList.setLayoutManager(manager2);
        registered_todoList.setTag(viewType2);


    }

    private RecyclerView old_list;

    private void show_selected_oldTodo() {
        if (old_datas.size() > 0) {
            oldTodoView = inflater.inflate(R.layout.dialog_old_todos, null);
            old_list = (RecyclerView) oldTodoView.findViewById(R.id.old_todo_list);


            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            OrderedRealmCollection<Todo> datas = realmController.readTodoByType(Todo.OLD);
            final OldAdapter oldAdapter = new OldAdapter(getContext(),datas,true);
            Log.e("old_list", oldAdapter + "");
            RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            old_list.setLayoutManager(manager);
            old_list.setAdapter(oldAdapter);
            old_list.setHasFixedSize(true);
            builder.setTitle("Old Todos......");
            builder.setIcon(R.drawable.pflargeicon_two);
            builder.setView(oldTodoView);
            builder.setPositiveButton("Select", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    map_unRegister_adapters.get(OLD).addDatas(oldAdapter.popData());
                }
            });
            builder.setCancelable(false);
            builder.show();
        }


    }

    //데이트와 맞는 어뎁터가 없을시 생성하고 불러온 데이터를 현재 선택한 날짜의 어뎁터에 추가합니다.
    private void init_topAdapter() {
        if (!map_register_adapters.containsKey(selcted_day)) {
            map_register_adapters.put(selcted_day, new RegisteredAdapter(dragListener, this));
        }
        if (total_datas != null) {
            settingSelectedDate();
        }

    }

    private void settingSelectedDate() {
        for (SelectedTodo sTodo : total_datas) {
            if (compare_date(sTodo.getBelongDate()).equals("same")) {
                map_register_adapters.get(selcted_day).addData(sTodo);
            }
        }
        registered_todoList.setAdapter(map_register_adapters.get(selcted_day));
    }


    //private HashMap<Date, ArrayList<SelectedTodo>> map_selected_datas;
    private ArrayList<SelectedTodo> total_datas;
    private ArrayList<Todo> old_datas;

    //기존의 디비를 불러와 입력합니다.
    private void init_todoData() {
        //todo 데이터 가져오기
        ArrayList<Todo> todos = Sample.createSampleTodo();
        ArrayList<SelectedTodo> sTodos = Sample.createSampleSelectedTodo();

        old_datas = new ArrayList<>();
        total_datas = new ArrayList<>();
        for (Todo todo : todos) {
            map_unRegister_adapters.get(todo.getType()).addData(todo);
        }

        for (SelectedTodo todo : sTodos) {
            if (compare_date(todo.getBelongDate()).equals("past")) {
                if (!todo.isDone() && todo.getType().equals(ONCE)) {
                    Todo temp_todo = modifi_selectedTodo(todo);
                    old_datas.add(temp_todo);
                } else if (todo.isDone()) {
                    total_datas.add(todo);
                }
            }
        }


    }

    private Todo modifi_selectedTodo(SelectedTodo todo) {
        Todo temp_todo = new Todo();
        temp_todo.setCreateDate(today);
        temp_todo.setType(ONCE);
        temp_todo.setContent(todo.getContent());
        temp_todo.setDone(false);
        return temp_todo;
    }


    //아래 어뎁터 생성후 맵에 연결합니다.
    private void init_bottomAdapter() {
        onceAdapter = new UnRegisterAdapter(dragListener);
        repeatAdapter = new UnRegisterAdapter(dragListener);
        oldAdapter = new UnRegisterAdapter(dragListener);
        map_unRegister_adapters.put(ONCE, onceAdapter);
        map_unRegister_adapters.put(REPEAT, repeatAdapter);
        map_unRegister_adapters.put(OLD, oldAdapter);
        unRegister_todoList.setAdapter(map_register_adapters.get(OLD));

    }


    //오늘 날짜를 저장하고 날짜 표시하는 함수 setDate()를 호출합니다.
    private void init_dateData() {
        cal = Calendar.getInstance(Locale.KOREA);
        today = modifi_customDate(Calendar.getInstance().getTime());
        setDate();
    }

    //시,분,초를 모두 0으로 초기화 해주는 함수입니다.
    public static Date modifi_customDate(Date date) {
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-d");
        ParsePosition pos = new ParsePosition(0);
        return date_format.parse(date_format.format(date), pos);
    }

    //받은 날짜를 각각 텍스트뷰에 값을 세팅하는 함수입니다.
    private void setDate() {
        selcted_day = modifi_customDate(cal.getTime());
        SimpleDateFormat yearMonth_format = new SimpleDateFormat("yyyy MM");
        SimpleDateFormat day_format = new SimpleDateFormat("E요일 \n d  ");

        tv_yearMonth.setText(yearMonth_format.format(selcted_day));
        tv_nDate.setText(day_format.format(selcted_day));
        init_topAdapter();
    }


    //각종 리스너를 생성 및 설정하는 함수 입니다.
    private void init_listener() {
        tv_goal.setOnClickListener(this);
        tv_yearMonth.setOnClickListener(this);
        ib_before.setOnClickListener(this);
        ib_next.setOnClickListener(this);

        dragListener = new TodoDragListener(this);
        selectItemListener = new TodoTypeSelectItemListener(this);

        ib_before.setOnDragListener(dragListener);
        ib_next.setOnDragListener(dragListener);
        sp_todoType.setOnItemSelectedListener(selectItemListener);
        registered_todoList.setOnDragListener(dragListener);
        unRegister_todoList.setOnDragListener(dragListener);

    }

    // tv_goal에 저장한 내용이 sharedPrefefences에 있으면 화면에 세팅해주는 함수 입니다.
    private void init_goal() {
        SharedPreferences pref = getActivity().getSharedPreferences("Goal", Context.MODE_PRIVATE);
        String old_goal = pref.getString("goal", "");
        if (old_goal.equals("")) {
            tv_goal.setHint("목표 또는 좌우명");
        } else {
            tv_goal.setText(old_goal);
        }
    }


    //프래그먼트 종료시 tv_goal에 적힌 내용을 sharedPreference에 저장합니다.
    @Override
    public void onDetach() {
        super.onDetach();
        SharedPreferences pref = getActivity().getSharedPreferences("Goal", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("goal", tv_goal.getText().toString());
        realmController.realmClose();
        editor.commit();
    }


    //클릭 시 발생하는 함수입니다.
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_before:
                changeDate(false);
                break;
            case R.id.ib_next:
                changeDate(true);
                break;
            case R.id.tv_goal:
                setGoal();
                parent.removeView(dialogView);
                break;
            case R.id.tv_yearMonth:
                showDatePick();
                break;
        }

    }


    //버튼을 눌러서 날짜를 변경하고 화면에 변경하는 함수입니다.
    private void changeDate(boolean next) {
        if (next) {
            cal.add(Calendar.DATE, 1);
        } else {
            cal.add(Calendar.DATE, -1);
        }
        setDate();

    }

    //달력 다이얼로그 창을 통해서 날짜를 선택하게 합니다.
    private void showDatePick() {
        DatePickerDialog dpd = DatePickerDialog.newInstance(TodoFragment.this,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
        dpd.setThemeDark(true);
        dpd.show(getActivity().getFragmentManager(), "");
    }

    //선택된 날짜를 화면에 세팅하는 함수입니다.
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        cal.set(year, monthOfYear, dayOfMonth);
        setDate();
    }

    // 목표 설정을 할 때 사용되는 다이얼로그 창을 띄우는 함수입니다.
    private void setGoal() {
        dialogView = inflater.inflate(R.layout.dialog_setgoal, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Setting Goal");
        builder.setIcon(R.drawable.pflargeicon_two);
        builder.setView(dialogView);
        builder.setPositiveButton("Complite", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText et_goal = (EditText) dialogView.findViewById(R.id.et_goal);
                String goal_saved = et_goal.getText().toString();
                if (goal_saved.length() == 0) {
                    tv_goal.setText("input your goal");
                }
                Log.e("dialog", "int i = " + i);
                tv_goal.setText(goal_saved);
            }
        });
        builder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    //오늘 날짜와 비교하여 값을 리턴하여 줍니다.
    @Override
    public String compare_date(Date belongDate) {
        if (today.compareTo(belongDate) == 0) {
            return "same";
        } else if (today.compareTo(belongDate) > 0) {
            return "past";
        } else {
            return "future";
        }
    }

    //선택한 아이템의 type에 맞는 어뎁터를 리스트뷰에 장착
    @Override
    public void setTodoType(String type) {
        String type_transform = "";
        if (type.equals("Once")) {
            type_transform = ONCE;
        } else if (type.equals("Repeat")) {
            type_transform = REPEAT;
        } else if (type.equals("Old")) {
            type_transform = OLD;
        }
        if (!type_transform.equals("")) {

            UnRegisterAdapter selected_adapter = map_unRegister_adapters.get(type_transform);
            if (selected_adapter != null)
                unRegister_todoList.setAdapter(selected_adapter);
        }

    }

    private boolean alive = true;


    //드래그 한 아이템이 버튼 영역에 갔을시 1초마다 runUIThread를 사용해 움직이게 한다.
    @Override
    public void moveDate(int bottonID) {
        alive = true;
        switch (bottonID) {
            case R.id.ib_before:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (alive) {
                            try {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        changeDate(false);
                                    }
                                });
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }).start();
                break;
            case R.id.ib_next:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (alive) {
                            try {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        changeDate(true);
                                    }
                                });
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }).start();
                break;
            default:
                break;
        }

    }

    //드래그 한 아이템이 버튼의 범위를 벗어났을시 스레드를 멈추게 한다.
    @Override
    public void stop() {
        alive = false;
    }


    //드래그한 아이템의 순서를 바꿔주는 함수 입니다.
    @Override
    public void swapPosition(int src, int tar, String adapterType) {
        map_unRegister_adapters.get(adapterType).swapPosition(src, tar);
    }


    //bottom 아이템을 이제 날짜에 종속시키는데 데이터 부분입니다.
    @Override
    public void register_todo(String pickedType, int pickedIndex, String targetType) {
        Date target_date = null;
        //target이 상단의 날짜 리스트뷰나 아이템일시 그 날의 날짜 데이트 획득
        if (targetType.equals("Today") || targetType.equals("Today_list")) {
            target_date = selcted_day;
        }


        //날짜 데이트가 있고 올리려는 날짜가 과거가 아닐 시
        if (target_date != null && !compare_date(selcted_day).equals("past")) {
            Todo pop_todo = new Todo();
            if (pickedType.equals(ONCE) || pickedType.equals(OLD)) {
                pop_todo = map_unRegister_adapters.get(pickedType).pop(pickedIndex);
                putTodo(pop_todo, target_date);
            } else if (pickedType.equals(REPEAT)) {
                pop_todo = map_unRegister_adapters.get(pickedType).get(pickedIndex);
                putTodo(pop_todo, target_date);
            }
            //과거일시
        } else {

            Log.e("error", "target_date is null!");
            Toast.makeText(getContext(), "지난 날짜에 추가 할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }

    }

    //todo를 sTodo 형태로 바꾸어서 register adapter에 올린다.
    private void putTodo(Todo pop_todo, Date target_date) {
        SelectedTodo sTodo = null;
        sTodo = modifi_todo(pop_todo, target_date);
        Log.e("sTodo", "stodo =" + sTodo.getContent());
        //있는지 중복 검사
        if (!isExistence(sTodo)) {
            map_register_adapters.get(target_date).addData(sTodo);
        } else {
            Toast.makeText(getContext(), "이미 올라간 메모입니다!!", Toast.LENGTH_SHORT).show();
        }
    }

    //중복 검사
    private boolean isExistence(SelectedTodo sTodo) {
        boolean result = false;
        Log.e("tag", map_register_adapters.get(selcted_day).isExistence(sTodo) + "");
        if (map_register_adapters.get(selcted_day).isExistence(sTodo))
            result = true;
        return result;
    }

    //todo를 selectedTodo 형태로 바꾼다.
    private SelectedTodo modifi_todo(Todo todo, Date target_date) {
        SelectedTodo temp_todo = new SelectedTodo();
        temp_todo.setDone(false);
        if (todo.getType().equals(OLD)) {
            temp_todo.setType(ONCE);
        } else {
            temp_todo.setType(todo.getType());
        }
        temp_todo.setContent(todo.getContent());
        temp_todo.setBelongDate(target_date);
        temp_todo.setPutDate(Calendar.getInstance().getTime());

        return temp_todo;
    }


    //top listview에서 드래그 시  선택한 아이템 타입에 맞는 스피너 아이템으로 변한다.
    @Override
    public void setSpinner(int index) {
        String type = map_register_adapters.get(selcted_day).getType(index);
        if (type.equals(ONCE)) {
            sp_todoType.setSelection(0);
        } else if (type.equals(REPEAT)) {
            sp_todoType.setSelection(1);
        } else {
            Log.e("error", "type is not correct");
        }

    }


    //selectedTodo의 beleong date를 변경하여 소속을 변경하는 함수입니다.
    @Override
    public void changeBelongDate(Date pickedBelongDate, int pickedIndex) {
        if (!compare_date(selcted_day).equals("past")) {
            SelectedTodo temp_todo = map_register_adapters.get(pickedBelongDate).pop(pickedIndex);
            temp_todo.setBelongDate(selcted_day);
            temp_todo.setPutDate(Calendar.getInstance().getTime());
            map_register_adapters.get(selcted_day).addData(temp_todo);

        } else {
            Toast.makeText(getContext(), "지난 날짜에 추가 할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void unRegister_mode(Date pickedBelongDate, int pickedIndex) {
        SelectedTodo sTodo = map_register_adapters.get(pickedBelongDate).pop(pickedIndex);
        String type = sTodo.getType();
        if (type.equals(ONCE)) {
            Todo todo = modifi_selectedTodo(sTodo);
            map_unRegister_adapters.get(todo.getType()).addData(todo);
        }

    }

    @Override
    public void change_mode(boolean isDlete) {
        if (isDlete) {
            delete_layout.setVisibility(View.VISIBLE);
            menu_layout.setVisibility(View.GONE);
        } else {
            delete_layout.setVisibility(View.GONE);
            menu_layout.setVisibility(View.VISIBLE);
        }

    }


    //드래깅한 아이템이 삭제버튼의 레이아웃으로 다가왔을시 색변화와 벗어났을시 원래대로 돌아가는 함수
    @Override
    public void isEntered(boolean check) {
        if (check) {
            delete_layout.setBackgroundColor(Color.BLUE);
        } else {
            delete_layout.setBackgroundColor(Color.WHITE);
        }

    }

    //드래깅한 아이템을 삭제하는 함수
    @Override
    public void delete(String pickedType, int pickedIndex, Date pickedBelongDate) {
        if (pickedType.equals(ONCE) || pickedType.equals(REPEAT) || pickedType.equals(OLD)) {
            map_unRegister_adapters.get(pickedType).removeData(pickedIndex);
        } else if (pickedType.equals("Today")) {
            if (pickedBelongDate != null) {
                map_register_adapters.get(pickedBelongDate).removeData(pickedIndex);
            } else {
                Log.e("error", "belong data is null ");
            }
        }
        isEntered(false);

    }
}
