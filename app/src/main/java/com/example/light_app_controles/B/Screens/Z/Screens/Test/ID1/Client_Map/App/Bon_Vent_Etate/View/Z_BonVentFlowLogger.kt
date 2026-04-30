package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View

import android.util.Log

/**
 * Single-place debug logger for the full "montant entry → bon creation → display" flow.
 *
 * Filter in Logcat:  tag:BVFlow
 *
 * Steps:
 *  ①  INPUT_CHANGE      – every digit typed in the OutlinedTextField
 *  ②  DONE_PRESSED      – keyboard Done: parsed values before VM call
 *  ③  VM_ENTRY          – ViewModel receives the call
 *  ④  LATEST_SIT        – result of finding the current New_Situation_Credit
 *  ⑤  VERSEMENT_CREATED – the new Versemment bon just built
 *  ⑥  NEW_SIT_CREATED   – the replacement New_Situation_Credit just built
 *  ⑦  LIST_UPDATED      – after list is written back to ActiveDatas
 *  ⑧  SCREEN_RECOMPOSE  – screen sees the new list, bucket counts
 *  ⑨  ITEM_RENDER       – each item composable receives its bon
 */
object BonVentFlowLogger {

    private const val TAG = "BVFlow"

    fun inputChange(raw: String, accepted: Boolean) =
        Log.d(TAG, "① INPUT_CHANGE  raw=\"$raw\"  accepted=$accepted")

    fun donePressedParsed(
        outVal: String,
        parsed: Int?,
        montant: Double,
        ancienSit: Int?,
        diff: Int,
        clientKey: String,
    ) = Log.d(
        TAG,
        "② DONE_PRESSED  out_val=\"$outVal\"  parsed=$parsed  montant=$montant" +
                "  ancienSit=$ancienSit  diff=$diff  clientKey=\"$clientKey\""
    )

    fun vmEntry(montant: Double, clientKey: String, periodKey: String, listSize: Int) =
        Log.d(
            TAG,
            "③ VM_ENTRY  montant=$montant  clientKey=\"$clientKey\"" +
                    "  periodKey=\"$periodKey\"  listSize=$listSize"
        )

    fun latestSitFound(found: Boolean, key: String?, oldMontant: Double?) =
        Log.d(
            TAG,
            "④ LATEST_SIT  found=$found  key=\"${key?.takeLast(8) ?: "-"}\"" +
                    "  oldMontant=$oldMontant"
        )

    fun versementCreated(key: String, versementFait: Double) =
        Log.d(
            TAG,
            "⑤ VERSEMENT_CREATED  key=\"${key.takeLast(8)}\"  versement_fait=$versementFait"
        )

    fun newSitCreated(key: String, newMontant: Double) =
        Log.d(
            TAG,
            "⑥ NEW_SIT_CREATED  key=\"${key.takeLast(8)}\"  newMontant=$newMontant"
        )

    fun listUpdated(list: List<M8BonVent>, captureRequested: Boolean) {
        val sitCount = list.count { it.etateActuellementEst == M8BonVent.EtateActuellementEst.New_Situation_Credit }
        val versCount = list.count { it.etateActuellementEst == M8BonVent.EtateActuellementEst.Versemment }
        val creditCount = list.count {
            it.etateActuellementEst == M8BonVent.EtateActuellementEst.Credit ||
                    it.etateActuellementEst == M8BonVent.EtateActuellementEst.Cette_Transaction_Type_Est_Credit
        }
        val nonCreditCount = list.count { !it.etateActuellementEst.credit_type }
        Log.d(
            TAG,
            "⑦ LIST_UPDATED  total=${list.size}  sit=$sitCount  vers=$versCount" +
                    "  credit=$creditCount  nonCredit=$nonCreditCount  captureRequested=$captureRequested"
        )
    }

    fun screenRecompose(
        allBons: Int,
        sitBons: Int,
        cvBons: Int,
        nonCreditBons: Int,
        latestSitKey: String?,
        latestSitMontant: Double?,
    ) = Log.d(
        TAG,
        "⑧ SCREEN_RECOMPOSE  allBons=$allBons  sitBons=$sitBons  cvBons=$cvBons" +
                "  nonCreditBons=$nonCreditBons  latestSitKey=\"${latestSitKey?.takeLast(8) ?: "-"}\"" +
                "  latestSitMontant=$latestSitMontant"
    )

    fun itemRender(etat: M8BonVent.EtateActuellementEst, key: String, montant: Double) =
        Log.d(
            TAG,
            "⑨ ITEM_RENDER  etat=${etat.name}  key=\"${key.takeLast(8)}\"  montant=$montant"
        )
}
