package io.jun.healthit.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import io.jun.healthit.R
import io.jun.healthit.view.RoutineActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

class RoutineFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_routine, container, false)

        MobileAds.initialize(this.context)
        val mAdView: AdView = root.findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        val intent = Intent(this.context, RoutineActivity::class.java)

        val cardViewFullBody:CardView = root.findViewById(R.id.cardView_full_body)
        cardViewFullBody.setOnClickListener {
            intent.putExtra("routineType", "full_body")
            startActivity(intent)
        }

        val carViewSplit2day: CardView = root.findViewById(R.id.cardView_split_2day)
        carViewSplit2day.setOnClickListener {
            intent.putExtra("routineType", "2day_split")
            startActivity(intent)
        }

        val cardViewSplit3day: CardView = root.findViewById(R.id.cardView_split_3day)
        cardViewSplit3day.setOnClickListener {
            intent.putExtra("routineType", "3day_split")
            startActivity(intent)
        }

        val cardViewSplit4day: CardView = root.findViewById(R.id.cardView_split_4day)
        cardViewSplit4day.setOnClickListener {
            intent.putExtra("routineType", "4day_split")
            startActivity(intent)
        }

        val cardViewSplit5day: CardView = root.findViewById(R.id.cardView_split_5day)
        cardViewSplit5day.setOnClickListener {
            intent.putExtra("routineType", "5day_split")
            startActivity(intent)
        }
        val cardViewStrength: CardView = root.findViewById(R.id.cardView_strength)
        cardViewStrength.setOnClickListener {
            intent.putExtra("routineType", "strength")
            startActivity(intent)
        }

        return root
    }
}