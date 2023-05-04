package com.example.niwansu_android_application.core;

import static android.content.Context.MODE_PRIVATE;
import static com.example.niwansu_android_application.core.Constants.PREFERENCE_NAME;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.niwansu_android_application.R;
import com.example.niwansu_android_application.models.changeStatusModel;
import com.example.niwansu_android_application.screens.doctor.activities.DoctorMainActivity;
import com.example.niwansu_android_application.screens.doctor.activities.VideoCallDoctorActivity;
import com.zegocloud.uikit.prebuilt.call.config.ZegoNotificationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterPatientAppointments extends RecyclerView.Adapter <AdapterPatientAppointments.MyViewHolder>{

    private List<AppointmentsList> appointments;
    private Context context;
    SharedPreferences sharedPreferences;
    public AdapterPatientAppointments(List<AppointmentsList> appointments, Context context) {
        this.appointments = appointments;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_item, parent, false);
        return new MyViewHolder(view);


    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.appointmentNo.setText("Appointment No : "+appointments.get(position).getId());
        holder.Date.setText("" + appointments.get(position).getBookeddate());
        holder.doctor.setText(""+appointments.get(position).getDoctorname());
        holder.time.setText("" + appointments.get(position).getTime());
        holder.status.setText(""+appointments.get(position).getDoctorapprovalstatus());
        String patientname = appointments.get(position).getPatientname();
        sharedPreferences =context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        String category=  sharedPreferences.getString(Constants.KEY_USER_CATEGORY,null);
        String userID=  sharedPreferences.getString(Constants.KEY_EMAIL,null);
        //String username=  sharedPreferences.getString(Constants.KEY_FIRST_NAME,null);

        if(category.equals("doctor"))
        {
            if(appointments.get(position).getDoctorapprovalstatus().equals("Accepted"))
            {

                holder.btn_reject.setVisibility(View.INVISIBLE);
                holder.btn_accept.setVisibility(View.INVISIBLE);
                holder.spin_time.setVisibility(View.INVISIBLE);

            }
            else if(appointments.get(position).getDoctorapprovalstatus().equals("Rejected"))
             {
                holder.btn_reject.setVisibility(View.GONE);
                holder.btn_accept.setVisibility(View.GONE);
                holder.btn_video.setVisibility(View.GONE);
                 holder.spin_time.setVisibility(View.INVISIBLE);
             }
            else
            {

                holder.btn_video.setVisibility(View.GONE);
                holder.time.setVisibility(View.INVISIBLE);
            }
        }
        else {
            if(appointments.get(position).getDoctorapprovalstatus().equals("Accepted"))
            {

                holder.btn_reject.setVisibility(View.GONE);
                holder.btn_accept.setVisibility(View.GONE);
                holder.spin_time.setVisibility(View.GONE);
                holder.btn_video.setVisibility(View.GONE);

            }
            else if(appointments.get(position).getDoctorapprovalstatus().equals("Rejected"))
            {
                holder.btn_reject.setVisibility(View.GONE);
                holder.btn_accept.setVisibility(View.GONE);
                holder.btn_video.setVisibility(View.GONE);
                holder.btn_video_patient.setVisibility(View.GONE);
                holder.spin_time.setVisibility(View.GONE);
            }
            else
            {
                holder.btn_video_patient.setVisibility(View.GONE);
                holder.btn_reject.setVisibility(View.GONE);
                holder.btn_accept.setVisibility(View.GONE);
                holder.btn_video.setVisibility(View.GONE);
                holder.spin_time.setVisibility(View.GONE);

            }
        }

holder.btn_accept.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        String timeslot=holder.spin_time.getSelectedItem().toString();
        NetworkService apiInterface;
        apiInterface = NetworkClient.getClient().create(NetworkService.class);
        Call<changeStatusModel> call = apiInterface.changeStatus(appointments.get(position).getId(),"Accepted",timeslot);
        call.enqueue(new Callback<changeStatusModel>() {
            @Override
            public void onResponse(Call<changeStatusModel> call, Response<changeStatusModel> response) {
                if(response.body().getStatus().equals("ok"))
                {
                    Toast.makeText(context.getApplicationContext(),"Successfully Accepted",Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(context.getApplicationContext(), DoctorMainActivity.class);
                    intent.addFlags((Intent.FLAG_ACTIVITY_NEW_TASK));
                    context.startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<changeStatusModel> call, Throwable t) {
                Toast.makeText(context.getApplicationContext(),"Error While Connecting",Toast.LENGTH_LONG).show();
            }
        });
    }
});

        holder.btn_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NetworkService apiInterface;
                apiInterface = NetworkClient.getClient().create(NetworkService.class);
                Call<changeStatusModel> call = apiInterface.changeStatus(appointments.get(position).getId(),"Rejected","");
                call.enqueue(new Callback<changeStatusModel>() {
                    @Override
                    public void onResponse(Call<changeStatusModel> call, Response<changeStatusModel> response) {
                        if(response.body().getStatus().equals("ok"))
                        {
                            Toast.makeText(context.getApplicationContext(),"Successfully Rejected",Toast.LENGTH_LONG).show();
                            Intent intent=new Intent(context.getApplicationContext(), DoctorMainActivity.class);
                            intent.addFlags((Intent.FLAG_ACTIVITY_NEW_TASK));
                            context.startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Call<changeStatusModel> call, Throwable t) {
                        Toast.makeText(context.getApplicationContext(),"Error While Connecting",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        holder.btn_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = patientname;


                if (userID.isEmpty())
                {
                    return;
                }


                startService(userID);
                Intent intent = new Intent(context.getApplicationContext(), VideoCallDoctorActivity.class);
                intent.putExtra("userID",userID);
                intent.putExtra("UserName",username );
                context.startActivity(intent);
            }
        });

        holder.btn_video_patient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String username= holder.doctor.getText().toString();
                Toast.makeText(context, patientname, Toast.LENGTH_SHORT).show();

                if (userID.isEmpty())
                {
                    return;
                }


                startService(userID);
                Intent intent = new Intent(context.getApplicationContext(), VideoCallDoctorActivity.class);
                intent.putExtra("userID",userID);
                intent.putExtra("UserName",username );
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView appointmentNo,Date,doctor,time,status;
        Button btn_video,btn_accept,btn_reject,btn_video_patient;
        Spinner spin_time;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            appointmentNo = itemView.findViewById(R.id.appointmentNo);
            Date = itemView.findViewById(R.id.Date);
            doctor = itemView.findViewById(R.id.doctor);
            time = itemView.findViewById(R.id.time);
            status = itemView.findViewById(R.id.status);
            spin_time=itemView.findViewById(R.id.spin_time);
            btn_accept=itemView.findViewById(R.id.btn_accept);
            btn_reject=itemView.findViewById(R.id.btn_reject);
            btn_video=itemView.findViewById(R.id.btn_video);
            btn_video_patient=itemView.findViewById(R.id.btn_video_patient);

        }


    }

    void startService(String userID)
    {
        Application application = (Application) context.getApplicationContext(); // Android's application context
        long appID = 74354130;   // yourAppID
        String appSign ="35abdd92b089f965715a57126d71bf258d228c9e586abc432cc52853a34f1b0d";  // yourAppSign
        // yourUserID, userID should only contain numbers, English characters, and '_'.
        String userName =userID;   // yourUserName

        ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig = new ZegoUIKitPrebuiltCallInvitationConfig();
        callInvitationConfig.notifyWhenAppRunningInBackgroundOrQuit = true;
        ZegoNotificationConfig notificationConfig = new ZegoNotificationConfig();
        notificationConfig.sound = "zego_uikit_sound_call";
        notificationConfig.channelID = "CallInvitation";
        notificationConfig.channelName = "CallInvitation";
        ZegoUIKitPrebuiltCallInvitationService.init((Application) context.getApplicationContext(), appID, appSign, userID, userName,callInvitationConfig);
    }



}
