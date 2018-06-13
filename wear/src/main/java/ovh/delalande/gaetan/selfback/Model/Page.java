package ovh.delalande.gaetan.selfback.Model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ovh.delalande.gaetan.selfback.R;
import ovh.delalande.gaetan.selfback.Util.SenderService;

@SuppressLint("ValidFragment")
public class Page extends Fragment{

    private static final String TAG = "SelfBACK/Pager";
    private String pageLabel;
    private int pageImagePath;

    private SenderService senderService;

    public View rootView;
    public ImageView imageImageView;

    public Page(Context context, int pageLabel, int pageImagePath){
        this.pageLabel = context.getString(pageLabel);
        this.pageImagePath = pageImagePath;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.page_fragment, container, false);
        senderService = SenderService.getInstance(rootView.getContext());

        final TextView labelTextView = (TextView) rootView.findViewById(R.id.page_label);
        labelTextView.setText(pageLabel);

        imageImageView = (ImageView) rootView.findViewById(R.id.page_image);
        imageImageView.setImageResource(pageImagePath);

        imageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick");
                senderService.onClickActivity(rootView, pageLabel);
            }
        });

        return rootView;
    }
}
