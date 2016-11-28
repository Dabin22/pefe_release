package com.pefe.pefememo.app.fragments.todo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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

import com.pefe.pefememo.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TodoFragment extends Fragment implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener, TodoHandler {

    public TodoFragment() {
        // Required empty public constructor
    }


    public static TodoFragment newInstance(String param1, String param2) {
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
    private TextView tv_goal,tv_yearMonth,tv_nDate;
    private ImageButton ib_before,ib_next;
    private RecyclerView unRegister_todoList,registered_todoList;
    private Spinner sp_todoType;
    private LinearLayout delete_layout,menu_layout;

    //goal dialog 창에 관련된 것들
    private LayoutInflater inflater;
    private ViewGroup parent;
    private View dialogView;


    /*
        today : 오늘 날짜
        selected_day : 선택된 날짜
        cal : calendar 변수
     */
    private Date today,selcted_day;
    private Calendar cal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_todo, container, false);
        this.inflater =inflater;
        parent = container;
        init(view);
        return view;
    }

    //초기화하는 함수입니다.
    private void init(View view) {
        tv_goal = (TextView)view.findViewById(R.id.tv_goal);
        tv_yearMonth = (TextView)view.findViewById(R.id.tv_nDate);
        tv_yearMonth = (TextView)view.findViewById(R.id.tv_yearMonth);
        ib_before = (ImageButton)view.findViewById(R.id.ib_before);
        ib_next = (ImageButton)view.findViewById(R.id.ib_next);
        unRegister_todoList = (RecyclerView)view.findViewById(R.id.unRegister_todoList);
        registered_todoList = (RecyclerView)view.findViewById(R.id.registered_todoList);
        sp_todoType = (Spinner)view.findViewById(R.id.sp_todoType);
        delete_layout = (LinearLayout)view.findViewById(R.id.delete_layout);
        menu_layout = (LinearLayout)view.findViewById(R.id.menu_layout);

        init_goal();
        init_dateData();
        init_listener();


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

        tv_yearMonth.setText(yearMonth_format.format(yearMonth_format));
        tv_nDate.setText(day_format.format(selcted_day));

    }


    //각종 리스너를 생성 및 설정하는 함수 입니다.
    private void init_listener() {
        tv_goal.setOnClickListener(this);
        tv_yearMonth.setOnClickListener(this);
        ib_before.setOnClickListener(this);
        ib_next.setOnClickListener(this);


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


    @Override
    public void setTodoType(String type) {

    }

    @Override
    public void moveDate(int bottonID) {

    }

    @Override
    public void swapPosition(int src, int tar, String adapterType) {

    }

    @Override
    public void register_todo(String pickedType, int pickedIndex, String targetType) {

    }

    @Override
    public void stop() {

    }

    @Override
    public void setSpinner(int index) {

    }

    @Override
    public void changeBelongDate(Date pickedBelongDate, int pickedIndex) {

    }

    @Override
    public void unRegister_mode(Date pickedBelongDate, int pickedIndex) {

    }

    @Override
    public void change_mode(boolean isDlete) {

    }

    @Override
    public void isEntered(boolean check) {

    }

    @Override
    public void delete(String pickedType, int pickedIndex, Date pickedBelongDate) {

    }
}
