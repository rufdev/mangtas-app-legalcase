package mangtas.legalcase.models;

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.seti2.models.*;
import com.rameses.osiris2.client.*
import com.rameses.osiris2.common.*;
import com.rameses.util.*;

class CaseCounselModel extends CrudFormModel{
    
    def getLookupUser(){
        return Inv.lookupOpener('etracsuser:lookup',[
                onselect :{
                    entity.user = it;
                },
            ])
    }
}