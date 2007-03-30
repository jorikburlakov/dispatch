package $package;

import java.io.Serializable;

import net.databinder.auth.AuthDataSession;
import net.databinder.auth.IAuthSettings;
import wicket.MetaDataKey;
import wicket.markup.html.WebMarkupContainer;
import wicket.markup.html.basic.Label;
import wicket.markup.html.link.Link;
import wicket.markup.html.panel.Panel;
import wicket.model.AbstractReadOnlyModel;

/**
 * Displays sign in and out links, as well as current user if any.
 */
public class UserStatusPanel extends Panel {
	/**
	 * Constructs sign in and out links.
	 * @param id Wicket id
	 */
	public UserStatusPanel(String id) {
		super(id);

		WebMarkupContainer wrapper = new WebMarkupContainer("signedInWrapper") {
			public boolean isVisible() {
				return getAuthSession().isSignedIn();
			}
		};
		add(wrapper);
		wrapper.add(new Label("username", new AbstractReadOnlyModel() {
			@Override
			public Object getObject() {
				return getAuthSession().getUser().toString();
			}
		}));
		wrapper.add(new Link("profile") {
			@Override
			public void onClick() {
				if (getSession().getMetaData(inDetourKey) == null) {
					getSession().setMetaData(inDetourKey,  new InDetour());
					redirectToInterceptPage(new SignInPage(true));
				} else
					getSession().setMetaData(inDetourKey,  null);
			}
		});

		wrapper.add(new Link("signOut") {
			@Override
			public void onClick() {
				getAuthSession().signOut();
				setResponsePage(getApplication().getHomePage());
			}
		});

		add(getSignInLink("signIn"));
	}

	/**
	 * Returns link to sign-in page from <tt>AuthDataApplication</tt> subclass. Uses redirect
	 * to intercept page so that user will return to current page once signed in. Override
	 * for other behavior.
	 */
	protected Link getSignInLink(String id) {
		return new Link(id) {
			@Override
			public void onClick() {
				redirectToInterceptPage(getPageFactory().newPage(
						((IAuthSettings)getApplication()).getSignInPageClass()));
			}
			@Override
			public boolean isVisible() {
				return !getAuthSession().isSignedIn();
			}
		};
	}
	/** Session marker for editing profile */
	static class InDetour implements Serializable { }
	static MetaDataKey inDetourKey = new MetaDataKey(InDetour.class) { };

	/** @return casted web session*/
	protected AuthDataSession getAuthSession() {
		return (AuthDataSession) getSession();
	}
}
