package com.capstone.dogwhere

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_matching.*
import java.io.IOException
import java.util.*


class MatchingFragment : Fragment() {
    private lateinit var location: Location

    companion object {
        fun newInstance(): MatchingFragment {
            return MatchingFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var latitude: Double? = null
        var longitude: Double? = null
        val view = inflater.inflate(R.layout.activity_matching, container, false)
        var lm =
            this.requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGPSEnabled: Boolean = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled: Boolean = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        val areaText  = view.findViewById<TextView>(R.id.text_matching_address)

        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this.requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                0
            )
        } else {

            when {
                isNetworkEnabled -> {
                    try {
                        location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)!!
                        longitude = location.longitude
                        latitude = location.latitude
                    } catch (e: NullPointerException) {
                        Log.e("joo", "Network Exception!! : " + e)
                    }
                }
                isGPSEnabled -> {
                    try {
                        location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)!!
                        longitude = location.longitude
                        latitude = location.latitude
                    } catch (e: NullPointerException) {
                        Log.e("joo", "Gps Exception!! : " + e)
                    }

                }
            }
            Log.e("joo" , latitude.toString()+ longitude.toString())


            try {
                val area = getCurrentAddress(latitude!!, longitude!!)
                Log.e("joo", "주소 : $area ")
                areaText.text = area
            } catch (e: NullPointerException) {
                Log.e("ybybyb", "Network Exception!! : " + e)
            }

        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        btn_matching_chart.setOnClickListener {
            Intent(context, Search_Region::class.java).apply {
                putExtra("address_state", "matching_chart")
            }.run { startActivity(this) }
        }

        btn_matching_map.setOnClickListener {
            activity?.let {
                startActivity(Intent(context, MapsActivity::class.java))
                it.overridePendingTransition(R.anim.slide_up_enter, R.anim.slide_up_eixt)
            }
        }
        btn_registration_matching.setOnClickListener {
            activity?.let {
                startActivity(Intent(context, MatchingRegistrationActivity::class.java))
                it.overridePendingTransition(R.anim.slide_up_enter, R.anim.slide_up_eixt)
            }

        }
    }

    private fun getCurrentAddress(latitude: Double, longitude: Double): String {

        //지오코더... GPS를 주소로 변환
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses: List<Address>?
        addresses = try {
            geocoder.getFromLocation(
                latitude,
                longitude,
                7
            )
        } catch (ioException: IOException) {
            //네트워크 문제
            Toast.makeText(context, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show()
            return "지오코더 서비스 사용불가"
        } catch (illegalArgumentException: IllegalArgumentException) {
            Toast.makeText(context, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show()
            return "잘못된 GPS 좌표"
        }
        if (addresses == null || addresses.size == 0) {
            Toast.makeText(context, "주소 미발견", Toast.LENGTH_LONG).show()
            return "주소 미발견"
        }
        val address: Address = addresses[0]
        Log.e("joo", address.getAddressLine(0).toString())
        val area = address.getAddressLine(0).toString()
        val arealist = area.split(" ")
        return "${arealist[1]} ${arealist[2]} ${arealist[3]}"
    }

}