package mangtas.legalcase.models;

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.seti2.models.*;
import com.rameses.osiris2.client.*
import com.rameses.osiris2.common.*;
import com.rameses.util.*;
import tagabukid.utils.*;

class LegalCaseModel extends CrudFormModel{
    def selectedItem;
    def attachmentSelectedItem;
    
    @FormTitle
    def title

    public void afterCreate(){
        entity.state = "DRAFT";
    }
    
    public void beforeOpen(){
        title = entity.caseno + " - " + entity.title;
        loadAttachments()
    }
    
    def ledgerEntryHandler = [
        fetchList: { o->
            def p = [_schemaname: 'case_ledgerpayment_item'];
            p.findBy = [ 'parent.caseobjid': entity.objid ];
            p.select = "parent.objid,parent.receiptno,parent.receiptdate,account.title,amount,parent.remarks,parent.createdby_name,parent.dtcreated";
            return queryService.getList( p );
          
        }
    ] as BasicListModel;
    
    def followupEntryHandler = [
        fetchList: { o->
            def p = [_schemaname: 'case_followups'];
            p.findBy = [ 'caseobjid': entity.objid ];
            p.select = "dtstart,followuptype.name,title,description";
            return queryService.getList( p );
          
        }
    ] as BasicListModel;

    def capturePayment() {
        return Inv.lookupOpener("capture_case_ledgerpayment_item", [parent: entity ] );
    }
    
    def paymentDetail() {
        if(!selectedItem){
            throw new Exception("Please select a ledger item");
        }
        return Inv.lookupOpener("detail_case_ledgerpayment_item", [parent: selectedItem ] );
    }
    
    def addCaseFollowup() {
        return Inv.lookupOpener("case_followup_item", [parent: entity ] );
    }

    void refreshItem() {
        ledgerEntryHandler.reload();
    }
    void refreshFollowupItem() {
        followupEntryHandler.reload();
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
    
    def attachmentListHandler = [
        fetchList : { return entity.attachments },
    ] as BasicListModel
            
    void loadAttachments(){
        entity.attachments = [];
        try{
            entity.attachments = TagabukidDBImageUtil.getInstance().getImages(entity?.objid);
        }
        catch(e){
            println 'Load Attachment error ============';
            e.printStackTrace();
        }
        attachmentListHandler?.load();
    }
    
    def addAttachment(){
        return InvokerUtil.lookupOpener('upload:attachment', [
                entity : entity,
                afterupload: {
                    loadAttachments();
                }
            ]);
    }

    void deleteAttachment(){
        if (!attachmentSelectedItem) return;
        if (MsgBox.confirm('Delete selected Attachment?')){
            TagabukidDBImageUtil.getInstance().deleteImage(attachmentSelectedItem.objid);
            loadAttachments();
        }
    }


    def viewAttachment(){
        if (!attachmentSelectedItem) return null;

        if (attachmentSelectedItem.extension.contains("pdf")){
            return InvokerUtil.lookupOpener('attachmentpdf:view', [
                    entity : attachmentSelectedItem,
                ]); 
        }else{
            return InvokerUtil.lookupOpener('attachment:view', [
                    entity : attachmentSelectedItem,
                ]); 
        }

    }
    
    
}