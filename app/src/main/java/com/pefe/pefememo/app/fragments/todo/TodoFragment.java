package com.pefe.pefememo.app.fragments.todo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.pefe.pefememo.R;
import com.pefe.pefememo.model.todo.SelectedTodo;
import com.pefe.pefememo.model.todo.Todo;
import com.pefe.pefememo.realm.RealmController;
import com.pefe.pefememo.realm.RealmControllerImpl;
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
    private Button btn_calendar;
    private ImageButton ib_before, ib_next;
    private RecyclerView unRegister_todoList, registered_todoList;
    private Spinner sp_todoType;
    private ImageView iv_delete;
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
    private HashMap<Date, OrderedRealmCollection<SelectedTodo>> map_seletedTodo_datas;
    private UnRegisterAdapter once_type_adapter, repeat_type_adapter, old_type_adapter;
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
        btn_calendar = (Button)view.findViewById(R.id.btn_calendar);
        iv_delete = (ImageView)view.findViewById(R.id.iv_delete);
        map_register_adapters = new HashMap<>();
        map_unRegister_adapters = new HashMap<>();
        map_seletedTodo_datas = new HashMap<>();

        init_goal();

        init_dateData();
        init_todoData();
        init_listview();
        init_listener();
        init_bottomAdapter();
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
        if (old_type_datas.size() > 0) {
            oldTodoView = inflater.inflate(R.layout.dialog_old_todos, null);
            old_list = (RecyclerView) oldTodoView.findViewById(R.id.old_todo_list);
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            final OldAdapter oldAdapter = new OldAdapter(getContext(), old_type_datas, true);
            RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            old_list.setLayoutManager(manager);
            old_list.setAdapter(oldAdapter);
            old_list.setHasFixedSize(true);
            builder.setTitle("Delete Old Todo");
            builder.setIcon(R.drawable.pefelogo);
            builder.setView(oldTodoView);
            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ArrayList<Todo> remove_datas = oldAdapter.pop_remove_Datas();
                    for (Todo todo : remove_datas) {
                        realmController.deleteSelectedTodo(todo.getNo());
                    }
                }
            });
            builder.setCancelable(false);
            builder.show();
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        init_dateData();
    }

    //데이트와 맞는 어뎁터가 없을시 생성하고 불러온 데이터를 현재 선택한 날짜의 어뎁터에 추가합니다.
    private void init_topAdapter() {
        if (!map_register_adapters.containsKey(selcted_day)) {
            if (!map_seletedTodo_datas.containsKey(selcted_day)) {
                map_seletedTodo_datas.put(selcted_day, realmController.readSelectedTodoByBelongDate(selcted_day));
                Log.e("map", "before size = " + map_seletedTodo_datas.get(selcted_day).size());
            }
            if (compare_date(selcted_day).equals("past")) {
                for (SelectedTodo todo : map_seletedTodo_datas.get(selcted_day)) {
                    if (!todo.isDone()) {
                        if (todo.getType().equals(ONCE
                        )) {
                            Todo temp_todo = modifi_selectedTodo(todo);
                            realmController.writeTodo(OLD, temp_todo.getContent(), selcted_day);
                            realmController.deleteSelectedTodo(todo.getNo());
                        } else {
                            realmController.deleteSelectedTodo(todo.getNo());
                        }
                    }
                }
            }

            map_register_adapters.put(selcted_day, new RegisteredAdapter(getContext(), map_seletedTodo_datas.get(selcted_day), true, dragListener,this));
        }
        registered_todoList.setAdapter(map_register_adapters.get(selcted_day));
        Log.e("map", "after adpater = " + registered_todoList.getAdapter());
        Log.e("map", "after size = " + map_seletedTodo_datas.get(selcted_day).size());
    }


    private OrderedRealmCollection<Todo> once_type_datas;
    private OrderedRealmCollection<Todo> repeat_type_datas;
    private OrderedRealmCollection<Todo> old_type_datas;

    //기존의 디비를 불러와 입력합니다.
    private void init_todoData() {
        once_type_datas = realmController.readTodoByType(ONCE);
        repeat_type_datas = realmController.readTodoByType(REPEAT);
        old_type_datas = realmController.readTodoByType(OLD);

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
        once_type_adapter = new UnRegisterAdapter(getContext(), once_type_datas, true, dragListener);
        repeat_type_adapter = new UnRegisterAdapter(getContext(), repeat_type_datas, true, dragListener);
        old_type_adapter = new UnRegisterAdapter(getContext(), old_type_datas, true, dragListener);
        map_unRegister_adapters.put(ONCE, once_type_adapter);
        map_unRegister_adapters.put(REPEAT, repeat_type_adapter);
        map_unRegister_adapters.put(OLD, old_type_adapter);
        unRegister_todoList.setAdapter(map_register_adapters.get(ONCE));

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
        SimpleDateFormat yearMonth_format = new SimpleDateFormat("MM");
        SimpleDateFormat day_format = new SimpleDateFormat("d(E)");

        tv_yearMonth.setText(yearMonth_format.format(selcted_day));
        tv_nDate.setText(day_format.format(selcted_day));
        init_topAdapter();
    }


    //각종 리스너를 생성 및 설정하는 함수 입니다.
    private void init_listener() {
        tv_goal.setOnClickListener(this);
        btn_calendar.setOnClickListener(this);
        ib_before.setOnClickListener(this);
        ib_next.setOnClickListener(this);

        dragListener = new TodoDragListener(this);
        selectItemListener = new TodoTypeSelectItemListener(this);

        delete_layout.setOnDragListener(dragListener);
        ib_before.setOnDragListener(dragListener);
        ib_next.setOnDragListener(dragListener);
        init_spinnerAdapter();
        sp_todoType.setOnItemSelectedListener(selectItemListener);

        registered_todoList.setOnDragListener(dragListener);
        unRegister_todoList.setOnDragListener(dragListener);

    }

    private void init_spinnerAdapter(){
        ArrayList<String> items = new ArrayList<>();
        items.add("Once");
        items.add("Repeat");
        items.add("Old");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),R.layout.item_spinner_white,items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_todoType.setAdapter(adapter);
    }

    // tv_goal에 저장한 내용이 sharedPrefefences에 있으면 화면에 세팅해주는 함수 입니다.
    private void init_goal() {
        SharedPreferences pref = getActivity().getSharedPreferences("Goal", Context.MODE_PRIVATE);
        String old_goal = pref.getString("goal", "");
        if (old_goal.equals("")) {
            tv_goal.setHint("Goal");
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
            case R.id.btn_calendar:
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
        builder.setIcon(R.drawable.pefelogo);
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

    //bottom 아이템을 이제 날짜에 종속시키는데 데이터 부분입니다.
    @Override
    public void register_todo(String pickedType, int pickedIndex, String targetType) {
        //날짜 데이트가 있고 올리려는 날짜가 과거가 아닐 시
        if (!compare_date(selcted_day).equals("past")) {
            Todo pop_todo;
            if (pickedType.equals(ONCE)) {
                pop_todo = once_type_datas.get(pickedIndex);
                putTodo(pop_todo, selcted_day);

            } else if (pickedType.equals(OLD)) {
                pop_todo = old_type_datas.get(pickedIndex);
                putTodo(pop_todo, selcted_day);

            } else if (pickedType.equals(REPEAT)) {
                pop_todo = repeat_type_datas.get(pickedIndex);
                putTodo(pop_todo, selcted_day);
            }
            //과거일시
        } else {
            Toast.makeText(getContext(), "Cannot add on past", Toast.LENGTH_SHORT).show();
        }

    }

    //todo를 sTodo 형태로 바꾸어서 register adapter에 올린다.
    private void putTodo(Todo pop_todo, Date target_date) {
        SelectedTodo sTodo = null;
        sTodo = modifi_todo(pop_todo, target_date);
        //있는지 중복 검사
        if (!isExistence(sTodo)) {

            realmController.writeSelectedTodo(pop_todo.getNo(), sTodo.getType(), sTodo.getContent(), selcted_day, selcted_day);

        } else {
            Toast.makeText(getContext(), "Already registered", Toast.LENGTH_SHORT).show();
        }
    }

    //중복 검사
    private boolean isExistence(SelectedTodo sTodo) {
        for (SelectedTodo todos : map_seletedTodo_datas.get(selcted_day)) {
            if (todos.getContent().equals(sTodo.getContent()) && todos.getType().equals(sTodo.getType())) {
                return true;
            }
        }
        return false;
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
        temp_todo.setPutDate(today);
        return temp_todo;
    }


    //top listview에서 드래그 시  선택한 아이템 타입에 맞는 스피너 아이템으로 변한다.
    @Override
    public void setSpinner(int index) {
        String type = map_seletedTodo_datas.get(selcted_day).get(index).getType();
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
            SelectedTodo temp_todo = map_seletedTodo_datas.get(pickedBelongDate).get(pickedIndex);
            Log.e("compare_date",temp_todo.getNo()+"pick = " + temp_todo.getBelongDate());
            Log.e("compare_date","내가변화 시키고자 하는 데이트는 " +selcted_day);
            realmController.modifySelectedTodo(temp_todo.getNo(), temp_todo.isDone(), temp_todo.getType(), temp_todo.getContent(), selcted_day, temp_todo.getPutDate());
            Log.e("compare_date",temp_todo.getNo()+"pick after = " + temp_todo.getBelongDate());
        } else {
            Toast.makeText(getContext(), "Cannot add on past", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void unRegister_mode(Date pickedBelongDate, int pickedIndex) {
        SelectedTodo sTodo = map_seletedTodo_datas.get(pickedBelongDate).get(pickedIndex);

        String type = sTodo.getType();

        if (type.equals(ONCE)) {
            Todo todo = modifi_selectedTodo(sTodo);
            realmController.writeTodo(todo.getType(), todo.getContent(), selcted_day);

        }
        realmController.deleteSelectedTodo(sTodo.getNo());
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

    @Override
    public void change_done(SelectedTodo todo, boolean done) {
        Log.e("tag","get no = " + todo.getNo());
        realmController.modifySelectedTodo(todo.getNo(),done,todo.getType(),todo.getContent(),todo.getBelongDate(),todo.getPutDate());
    }

    //드래깅한 아이템이 삭제버튼의 레이아웃으로 다가왔을시 색변화와 벗어났을시 원래대로 돌아가는 함수
    @Override
    public void isEntered(boolean check) {
        if (check) {
            //delete_layout.setBackgroundResource(R.color.blueSecond);

            iv_delete.setImageResource(R.drawable.ic_delete_white_36dp);

        } else {
            //delete_layout.setBackgroundColor(Color.WHITE);
            iv_delete.setImageResource(R.drawable.ic_delete_red_36dp);
        }

    }

    //드래깅한 아이템을 삭제하는 함수
    @Override
    public void delete(String pickedType, int pickedIndex, Date pickedBelongDate) {
        Log.e("tag", "delete");
        final int tmep = pickedIndex;
        if (pickedType.equals(ONCE)) {
            Long no = once_type_datas.get(tmep).getNo();
            realmController.deleteTodo(no);
        } else if (pickedType.equals(REPEAT)) {
            realmController.deleteTodo(repeat_type_datas.get(pickedIndex).getNo());
        } else if (pickedType.equals(OLD)) {
            realmController.deleteTodo(old_type_datas.get(pickedIndex).getNo());
        } else if (pickedType.equals("Today")) {
            realmController.deleteSelectedTodo(map_seletedTodo_datas.get(pickedBelongDate).get(pickedIndex).getNo());
        }
        isEntered(false);

    }
}
