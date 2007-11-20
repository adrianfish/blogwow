package org.sakaiproject.blogwow.tool.producers;

import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.blogwow.model.constants.BlogConstants;
import org.sakaiproject.blogwow.tool.otp.EntryLocator;
import org.sakaiproject.blogwow.tool.params.BlogEntryParams;
import org.sakaiproject.blogwow.tool.params.SimpleBlogParams;

import uk.org.ponder.messageutil.MessageLocator;
import uk.org.ponder.rsf.components.UICommand;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIForm;
import uk.org.ponder.rsf.components.UIInput;
import uk.org.ponder.rsf.components.UIMessage;
import uk.org.ponder.rsf.components.UISelect;
import uk.org.ponder.rsf.components.UISelectChoice;
import uk.org.ponder.rsf.components.UIVerbatim;
import uk.org.ponder.rsf.components.decorators.DecoratorList;
import uk.org.ponder.rsf.components.decorators.UILabelTargetDecorator;
import uk.org.ponder.rsf.evolvers.TextInputEvolver;
import uk.org.ponder.rsf.flow.ARIResult;
import uk.org.ponder.rsf.flow.ActionResultInterceptor;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCase;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCaseReporter;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.SimpleViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParamsReporter;

public class AddEntryProducer implements ViewComponentProducer, ViewParamsReporter, NavigationCaseReporter, ActionResultInterceptor {

    public static final String VIEW_ID = "add_entry";
    public String getViewID() {
        return VIEW_ID;
    }

    private NavBarRenderer navBarRenderer;
    private TextInputEvolver richTextEvolver;
    private MessageLocator messageLocator;

    public void fillComponents(UIContainer tofill, ViewParameters viewparams, ComponentChecker checker) {

        UIMessage.make(tofill, "page-title", "blogwow.add_edit.title");

        BlogEntryParams params = (BlogEntryParams) viewparams;

        String entryLocator = "EntryLocator";
        String entryOTP = null;
        boolean newentry = false;
        if (params.entryid == null) {
            entryOTP = entryLocator + "." + EntryLocator.NEW_1;
            newentry = true;
        } else {
            entryOTP = entryLocator + "." + params.entryid;
        }

        navBarRenderer.makeNavBar(tofill, "navIntraTool:", VIEW_ID);

        if (newentry) {
            UIMessage.make(tofill, "add-entry-header", "blogwow.add_edit.addheader");
        } else {
            UIMessage.make(tofill, "add-entry-header", "blogwow.add_edit.editheader");
        }

        UIForm form = UIForm.make(tofill, "edit-blog-entry-form");

        UIMessage.make(form, "title-label", "blogwow.add_edit.title");
        UIInput.make(form, "title-input", entryOTP + ".title");

        UIInput blogtext = UIInput.make(form, "blog-text-input:", entryOTP + ".text");
        richTextEvolver.evolveTextInput(blogtext);

        UIMessage.make(form, "privacy-instructions", "blogwow.add_edit.accesstext");

        String[] privacyRadioValues = new String[] { 
                BlogConstants.PRIVACY_GROUP_LEADER, 
                BlogConstants.PRIVACY_GROUP,
                BlogConstants.PRIVACY_PUBLIC };
        String[] privacyRadioLabelKeys = new String[] { 
                "blogwow.add_edit.private", 
                "blogwow.add_edit.sitemembers",
        "blogwow.add_edit.public" };

				UISelect privacyRadios;
				if (newentry) {
        	privacyRadios = UISelect.make(form, "privacy-radio-holder", privacyRadioValues, privacyRadioLabelKeys,
						entryOTP + ".privacySetting", BlogConstants.PRIVACY_PUBLIC).setMessageKeys();
				} else {
					privacyRadios = UISelect.make(form, "privacy-radio-holder", privacyRadioValues, privacyRadioLabelKeys,
						entryOTP + ".privacySetting").setMessageKeys();				
				}

        String selectID = privacyRadios.getFullID();
        UISelectChoice instructorsOnlyRadio = UISelectChoice.make(form, "instructors-only-radio", selectID, 0);
        UIVerbatim instructorsOnlyLabel = UIVerbatim.make(form, "instructors-only-label", 
                messageLocator.getMessage("blogwow.add_edit.private"));
        instructorsOnlyLabel.decorators = new DecoratorList(new UILabelTargetDecorator(instructorsOnlyRadio));

        UISelectChoice allMembersRadio = UISelectChoice.make(form, "all-members-radio", selectID, 1);
        UIVerbatim allMembersLabel = UIVerbatim.make(form, "all-members-label", 
                messageLocator.getMessage("blogwow.add_edit.sitemembers"));
        allMembersLabel.decorators = new DecoratorList(new UILabelTargetDecorator(allMembersRadio));

        UISelectChoice publicViewableRadio = UISelectChoice.make(form, "public-viewable-radio", selectID, 2);
        UIVerbatim publicViewableLabel = UIVerbatim.make(form, "public-viewable-label", 
                messageLocator.getMessage("blogwow.add_edit.public"));
        publicViewableLabel.decorators = new DecoratorList(new UILabelTargetDecorator(publicViewableRadio));

        UICommand.make(form, "publish-button", UIMessage.make("blogwow.add_edit.publish"), entryLocator + ".publishAll");
        UICommand.make(form, "save-button", UIMessage.make("blogwow.add_edit.save"), entryLocator + ".saveAll");
        UICommand.make(form, "cancel-button", UIMessage.make("blogwow.add_edit.cancel"));
    }

    public ViewParameters getViewParameters() {
        return new BlogEntryParams();
    }

    public List<NavigationCase> reportNavigationCases() {
        List<NavigationCase> l = new ArrayList<NavigationCase>();
        l.add(new NavigationCase(null, new SimpleViewParameters(HomeProducer.VIEW_ID)));
        return l;
    }

    public void interceptActionResult(ARIResult result, ViewParameters incoming, Object actionReturn) {
        BlogEntryParams bep = (BlogEntryParams) incoming;
        if (bep.blogid != null) {
            result.resultingView = new SimpleBlogParams(BlogViewProducer.VIEW_ID, bep.blogid);
        }    
    }

    public void setMessageLocator(MessageLocator messageLocator) {
        this.messageLocator = messageLocator;
    }

    public void setNavBarRenderer(NavBarRenderer navBarRenderer) {
        this.navBarRenderer = navBarRenderer;
    }

    public void setRichTextEvolver(TextInputEvolver richTextEvolver) {
        this.richTextEvolver = richTextEvolver;
    }

}
