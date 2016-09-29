package mangtas.legalcase.models;

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.seti2.models.*;
import com.rameses.osiris2.client.*
import com.rameses.osiris2.common.*;
import com.rameses.util.*;

class CaseLedgerCapturePaymentModel extends CrudFormModel {

    def parent;
    def selectedItem;
    def aftercapture;
     def getLookupAccount(){
        return Inv.lookupOpener('revenueitem:lookup',[
                onselect :{
                    selectedItem.account = it;
                },
            ])
    }
    
    public void afterCreate() {
        entity.items = [];
        entity.caseobjid = parent.objid;
    }
    
    public void afterSave(){
        if (aftercapture ) aftercapture();
    }
    public void beforeOpen(){
        entity.objid = parent.parent.objid;
        entity._schemaname = 'case_ledgerpayment';
//        println entity;
    }
 
    void updateAmount() {
        entity.amount = 0;
        entity.amount = entity.items.sum{x-> x.amount };
        binding.refresh("entity.amount");
    }
    
    def itemListModel = [
        fetchList: {
            return entity.items;
        },
        onAddItem: { o->
            addItem("items", o);
            updateAmount();
        },
        onRemoveItem: { o->
            removeItem("items", o);
            updateAmount();
        },
        onColumnUpdate: { o,colName ->
            if(colName=='amount' ) updateAmount();
        }
    ] as EditorListModel;
}