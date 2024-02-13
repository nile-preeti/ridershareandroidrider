package com.rideshare.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rideshare.app.R;
import com.rideshare.app.interfaces.CheckOptionClick;
import com.rideshare.app.pojo.payment.CardDetail;

import java.util.List;

public class PaymentMethodAdapter extends RecyclerView.Adapter<PaymentMethodAdapter.PaymentViewHolder> {

    private List<CardDetail> cardDetails;
    private int selectedPosition = -1;
    private Context context;
    private CheckOptionClick checkOptionClick;
    public PaymentMethodAdapter(List<CardDetail> cardDetails, CheckOptionClick checkOptionClick) {
        this.cardDetails = cardDetails;
        this.checkOptionClick = checkOptionClick;
    }

    @NonNull
    @Override
    public PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.payment_recyclerview_layout, parent, false);
        return new PaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PaymentViewHolder holder, int position) {
        holder.txtCardNumber.setText(cardDetails.get(position).getCardNumber());
        holder.txtExpiryDate.setText(cardDetails.get(position).getExpiryMonth() + "/" +
                cardDetails.get(position).getExpiryDate());
        holder.txtNameOnCard.setText(cardDetails.get(position).getCardHolderName());
        if(cardDetails.get(position).getIsDefault().equals("1"))
        {
            holder.chkDefault.setChecked(true);
            holder.btnDefault.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.chkDefault.setChecked(false);
            holder.btnDefault.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return cardDetails.size();
    }


    public class PaymentViewHolder extends RecyclerView.ViewHolder {

        public CheckBox chkDefault;
        public TextView txtCardNumber, txtNameOnCard, txtExpiryDate;
        public Button btnDefault, btnDelete;

        public PaymentViewHolder(@NonNull View itemView) {
            super(itemView);
            chkDefault = itemView.findViewById(R.id.chk_default);
            txtCardNumber = itemView.findViewById(R.id.tv_card_number);
            txtExpiryDate = itemView.findViewById(R.id.tv_card_expiry);
            txtNameOnCard = itemView.findViewById(R.id.tv_card_name);
            btnDefault = itemView.findViewById(R.id.btn_default);
            btnDefault.setVisibility(View.GONE);
            selectedPosition = getAdapterPosition();
//            SetCustomFont setCustomFont = new SetCustomFont();
//            setCustomFont.overrideFonts(context,itemView);
            chkDefault.setOnClickListener(e->
            {
                checkOptionClick.onClick(getAdapterPosition(),chkDefault.isChecked());
            });



        }
    }


}
