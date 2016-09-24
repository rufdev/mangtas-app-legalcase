package mangtas.legalcase.models;

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.seti2.models.*;
import com.rameses.osiris2.client.*
import com.rameses.osiris2.common.*;
import com.rameses.util.*;

class LegalCaseModel extends CrudFormModel{
    
    void beforsave(o){
        if (o == 'create') entity.state = "DRAFT";
    }
    def ledgerEntryHandler = [
        fetchList: { o->
            def p = [_schemaname: 'case_ledgerpayment_item'];
            p.findBy = [ 'parent.caseobjid': entity.objid ];
            p.select = "objid,parent.receiptno,parent.receiptdate,amount";
            return queryService.getList( p );
          
        }
    ] as BasicListModel;

    def capturePayment() {
        return Inv.lookupOpener("housing_ledger_capture_payment", [parent: entity ] );
    }

    void refreshItem() {
        ledgerEntryHandler.reload();
    }
    
    def getLookupCaseType(){
        return Inv.lookupOpener('casetypes:lookup',[
                onselect :{
                    entity.casetype = it;
                    binding.refresh(); 
                },
            ])
    }
    
    def getLookupCourt(){
        return Inv.lookupOpener('courts:lookup',[
                onselect :{
                    entity.court = it;
                    binding.refresh();
                },
            ])
    }
    
    def getLookupStatus(){
        return Inv.lookupOpener('status:lookup',[
                onselect :{
                    entity.status = it;
                    binding.refresh();
                },
            ])
    }
    
    def getLookupCounsel(){
        return Inv.lookupOpener('counsels:lookup',[
                onselect :{
                    
                    entity.counsel = [
                        objid: it.objid,
                        name: it.user.name,
                        jobtitle:it.user.jobtitle
                    ];
                    binding.refresh();
                },
            ])
    }
    
    
}