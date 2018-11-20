package com.github.esabook.speechtx.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.github.esabook.speechtx.R;
import com.github.esabook.speechtx.databinding.ItemDictionaryCounterBinding;
import com.github.esabook.speechtx.models.DictionaryDTO;

import java.util.Comparator;
import java.util.List;

public class DictionaryCounterAdapter extends ArrayAdapter<DictionaryDTO> {

    public DictionaryCounterAdapter(@NonNull Context context, int resource, @NonNull List<DictionaryDTO> objects) {
        super(context, resource, objects);
        sortDescending();
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ItemDictionaryCounterBinding mBinding;

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dictionary_counter, null);
            mBinding = DataBindingUtil.bind(convertView);
            convertView.setTag(mBinding);
        } else {
            mBinding = (ItemDictionaryCounterBinding) convertView.getTag();
        }

        mBinding.setModel(getItem(position));

        if (getItem(position).getHighlighted()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getItem(position).setHighlighted(false);
                    mBinding.setModel(getItem(position));
                }
            }, 2000);
        }
        return mBinding.getRoot();
    }


    @Override
    public void notifyDataSetChanged() {
        sortDescending();
        super.notifyDataSetChanged();
    }

    public void notifyDataSetChangedNoSort() {
        super.notifyDataSetChanged();
    }

    private void sortDescending() {
        setNotifyOnChange(false);
        sort(new Comparator<DictionaryDTO>() {
            @Override
            public int compare(DictionaryDTO o1, DictionaryDTO o2) {
                return Float.compare(o2.getFrequency(), o1.getFrequency());
            }
        });
        setNotifyOnChange(true);
    }

}
