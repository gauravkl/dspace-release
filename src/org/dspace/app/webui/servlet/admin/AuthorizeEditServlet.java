/*
 * AuthorizeEditServlet.java - policy editor servlet 
 *
 * $Id$
 *
 * Copyright (c) 2001, Hewlett-Packard Company and Massachusetts
 * Institute of Technology.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * - Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * - Neither the name of the Hewlett-Packard Company nor the name of the
 * Massachusetts Institute of Technology nor the names of their
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE. 
 */

package org.dspace.app.webui.servlet.admin;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.List;

import org.dspace.app.webui.servlet.DSpaceServlet;
import org.dspace.app.webui.util.JSPManager;
import org.dspace.app.webui.util.UIUtil;

import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.AuthorizeManager;
import org.dspace.authorize.ResourcePolicy;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;


/**
 * Servlet for editing permissions
 * @author dstuve
 * @version $Revision$
 */
public class AuthorizeEditServlet extends DSpaceServlet
{
    protected void doDSGet(Context c,
                    HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException, SQLException, AuthorizeException
    {
        // show the main page (select communities, collections, etc)
        showMainPage(c, request, response);
    }
    
    protected void doDSPost(Context c,
                    HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException, SQLException, AuthorizeException
    {
        String button = UIUtil.getSubmitButton(request, "submit");
        
        if( button.equals("submit_collection") )
        {
            // select a collection to work on
            Collection [] collections = Collection.findAll(c);
        
            request.setAttribute("collections", collections);
            JSPManager.showJSP(request, response, "/admin/collection_select.jsp" );
        }
        else if( button.equals("submit_community") )
        {
            // select a community to work on
            Community [] communities = Community.findAll(c);
            
            request.setAttribute("communities", communities);
            JSPManager.showJSP(request, response, "/admin/community_select.jsp" );            
        }
        else if( button.equals("submit_collection_select") )
        {
            // edit the collection's permissions
            Collection collection      = Collection.find(c,
                UIUtil.getIntParameter(request, "collection_id"));
            List policies = AuthorizeManager.getPolicies(c, collection);
            
            request.setAttribute("collection", collection );
            request.setAttribute("policies", policies     );
            JSPManager.showJSP(request, response,
                "/admin/authorize_collection_edit.jsp" );
        }
        else if( button.equals("submit_community_select") )
        {
            // edit the collection's permissions
            Community target = Community.find(c,
                UIUtil.getIntParameter(request, "community_id"));
            List policies = AuthorizeManager.getPolicies(c, target);
            
            request.setAttribute("community", target );
            request.setAttribute("policies", policies     );
            JSPManager.showJSP(request, response,
                "/admin/authorize_community_edit.jsp" );
        }
        else if( button.equals("submit_collection_delete_policy") )
        {
            // delete a permission from a collection
            Collection collection = Collection.find(c, UIUtil.getIntParameter(request, "collection_id"));
            ResourcePolicy policy = ResourcePolicy.find(c, UIUtil.getIntParameter(request, "policy_id"));
            
            // do the remove
            policy.delete();
            
            // return to collection permission page
            request.setAttribute("collection", collection );

            List policies = AuthorizeManager.getPolicies(c, collection);
            request.setAttribute("policies", policies);

            JSPManager.showJSP(request, response, "/admin/authorize_collection_edit.jsp" );
        }
        else if( button.equals("submit_community_delete_policy") )
        {
            // delete a permission from a collection
            Community community = Community.find(c,
                UIUtil.getIntParameter(request, "community_id"));
            ResourcePolicy policy = ResourcePolicy.find(c,
                UIUtil.getIntParameter(request, "policy_id"));
            
            // do the remove
            policy.delete();
            
            // return to collection permission page
            request.setAttribute("community", community );

            List policies = AuthorizeManager.getPolicies(c, community);
            request.setAttribute("policies", policies);

            JSPManager.showJSP(request, response,
                "/admin/authorize_community_edit.jsp" );
        }
        else if( button.equals("submit_collection_edit_policy") )
        {
            // edit a collection's policy - set up and call policy editor
            Collection collection = Collection.find(c, UIUtil.getIntParameter(request, "collection_id"));
            
            int policy_id = UIUtil.getIntParameter(request, "policy_id");
            ResourcePolicy policy = null;
            
            if( policy_id == -1 )
            {
                // create new policy
                policy = ResourcePolicy.create(c);
                policy.update();
            }
            else
            {
                policy = ResourcePolicy.find(c, policy_id);
            }
            
            Group   [] groups  = Group.findAll(c, Group.NAME);
            EPerson [] epeople = EPerson.findAll(c, EPerson.EMAIL);
            
            // return to collection permission page
            request.setAttribute( "edit_title", "Collection " + collection.getID() );
            request.setAttribute( "policy",     policy     );
            request.setAttribute( "groups",     groups     );
            request.setAttribute( "epeople",    epeople    );
            request.setAttribute( "id_name",    "collection_id" );
            request.setAttribute( "id",         "" + collection.getID() );
            JSPManager.showJSP(request, response, "/admin/authorize_policy_edit.jsp" );
        }
        else if( button.equals("submit_community_edit_policy") )
        {
            // edit a community's policy - set up and call policy editor
            Community community = Community.find(c,
                UIUtil.getIntParameter(request, "community_id"));
            
            int policy_id = UIUtil.getIntParameter(request, "policy_id");
            ResourcePolicy policy = null;
            
            if( policy_id == -1 )
            {
                // create new policy
                policy = ResourcePolicy.create(c);
                policy.update();
            }
            else
            {
                policy = ResourcePolicy.find(c, policy_id);
            }
            
            Group   [] groups  = Group.findAll  (c, Group.NAME   );
            EPerson [] epeople = EPerson.findAll(c, EPerson.EMAIL);
            
            // return to collection permission page
            request.setAttribute( "edit_title", "Community " + community.getID() );
            request.setAttribute( "policy",     policy     );
            request.setAttribute( "groups",     groups     );
            request.setAttribute( "epeople",    epeople    );
            request.setAttribute( "id_name",    "community_id" );
            request.setAttribute( "id",         "" + community.getID() );
            JSPManager.showJSP(request, response, "/admin/authorize_policy_edit.jsp" );
        }
        else if( button.equals( "submit_collection_add_policy") )
        {
            // want to add a policy, create an empty one and invoke editor
            ResourcePolicy policy = ResourcePolicy.create(c);
            policy.update();

            Collection collection = Collection.find(c,
                            UIUtil.getIntParameter(request, "collection_id"));
            
            Group   [] groups  = Group.findAll  (c, Group.NAME   );
            EPerson [] epeople = EPerson.findAll(c, EPerson.EMAIL);
            
            // return to collection permission page
            request.setAttribute( "edit_title", "Collection " + collection.getID() );
            request.setAttribute( "policy",     policy     );
            request.setAttribute( "groups",     groups     );
            request.setAttribute( "epeople",    epeople    );
            request.setAttribute( "id_name",    "collection_id" );
            request.setAttribute( "id",         "" + collection.getID() );
            
            JSPManager.showJSP(request, response,
                "/admin/authorize_policy_edit.jsp" );
        }
        else if( button.equals( "submit_community_add_policy") )
        {
            // want to add a policy, create an empty one and invoke editor
            ResourcePolicy policy = ResourcePolicy.create(c);
            policy.update();

            Community community = Community.find(c,
                            UIUtil.getIntParameter(request, "community_id"));
            
            Group   [] groups  = Group.findAll  (c, Group.NAME   );
            EPerson [] epeople = EPerson.findAll(c, EPerson.EMAIL);
            
            // return to collection permission page
            request.setAttribute( "edit_title", "Community " + community.getID() );
            request.setAttribute( "policy",     policy     );
            request.setAttribute( "groups",     groups     );
            request.setAttribute( "epeople",    epeople    );
            request.setAttribute( "id_name",    "community_id" );
            request.setAttribute( "id",         "" + community.getID() );
            
            JSPManager.showJSP(request, response,
                "/admin/authorize_policy_edit.jsp" );
        }

        else if( button.equals( "submit_save_policy" ) )
        {
            int policy_id     = UIUtil.getIntParameter(request, "policy_id"    );
            int action_id     = UIUtil.getIntParameter(request, "action_id"    );
            int group_id      = UIUtil.getIntParameter(request, "group_id"     );
            int collection_id = UIUtil.getIntParameter(request, "collection_id");
            int community_id  = UIUtil.getIntParameter(request, "community_id");

//            boolean is_public = (request.getParameter("is_public")==null ? false: true );
  
            Collection collection = null;
            Community  community  = null;
            String display_page   = null;
            
            ResourcePolicy policy = ResourcePolicy.find(c, policy_id);
            Group group = Group.find(c, group_id);
            
            if( collection_id != -1 )
            {
                collection = Collection.find( c, collection_id );
                
                // modify the policy    
                policy.setResource( collection );
                policy.setAction  ( action_id  );
                policy.setGroup   ( group      );
                policy.update();

                // set up page attributes
                request.setAttribute("collection", collection );
                request.setAttribute("policies",
                    AuthorizeManager.getPolicies( c, collection ) );
                display_page = "/admin/authorize_collection_edit.jsp";
            }
            
            if( community_id != -1 )
            {
                community = Community.find( c, community_id );

                // modify the policy
                policy.setResource( community );
                policy.setAction  ( action_id  );
                policy.setGroup   ( group      );
                policy.update();

                // set up page attributes
                request.setAttribute("community", community );
                request.setAttribute("policies",
                    AuthorizeManager.getPolicies( c, community ) );
                display_page = "/admin/authorize_community_edit.jsp";
            }

            // now return to previous state
            JSPManager.showJSP( request, response, display_page );
        }
        else if( button.equals("submit_cancel_policy") )
        {
            // return to the previous page
            int collection_id =UIUtil.getIntParameter (request, "collection_id");
            int community_id  =UIUtil.getIntParameter (request, "community_id" );

            String display_page = null;
            
            if( collection_id != -1 )
            {
                // set up for return to collection edit page
                Collection t = Collection.find( c, collection_id );

                request.setAttribute("collection", t );
                request.setAttribute("policies",
                    AuthorizeManager.getPolicies( c, t ) );
                display_page = "/admin/authorize_collection_edit.jsp";
            }
            else if( community_id != -1 )
            {
                // set up for return to community edit page
                Community t = Community.find( c, community_id );

                request.setAttribute("community", t );
                request.setAttribute("policies",
                    AuthorizeManager.getPolicies( c, t ) );
                display_page = "/admin/authorize_community_edit.jsp";
                
            }

            JSPManager.showJSP(request, response, display_page );
        }
        else
        {
            // return to the main page
            showMainPage(c, request, response);
        }
        
        c.complete();
    }
    
    void showMainPage(Context c,
                    HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException, SQLException, AuthorizeException
    {
        JSPManager.showJSP(request, response, "/admin/authorize_edit_main.jsp" );
    }
}
