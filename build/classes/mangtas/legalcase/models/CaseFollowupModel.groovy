package mangtas.legalcase.models;

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.seti2.models.*;
import com.rameses.osiris2.client.*
import com.rameses.osiris2.common.*;
import com.rameses.util.*;

class CaseFollowupModel extends CrudFormModel {

    def parent;
    def selectedItem;
    
    def getLookupFollowupType(){
        return Inv.lookupOpener('casefollowuptype:lookup',[
                onselect :{
                    entity.followuptype = it;
                    binding.refresh(); 
                },
            ])
    }
    
    public void afterCreate() {
        entity.caseobjid = parent.objid;
    }
 
}