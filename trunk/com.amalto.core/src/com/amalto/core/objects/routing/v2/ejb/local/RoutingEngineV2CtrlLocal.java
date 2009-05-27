/*
 * Generated by XDoclet - Do not edit!
 */
package com.amalto.core.objects.routing.v2.ejb.local;

/**
 * Local interface for RoutingEngineV2Ctrl.
 * @xdoclet-generated at 27-05-09
 * @copyright The XDoclet Team
 * @author XDoclet
 * @version ${version}
 */
public interface RoutingEngineV2CtrlLocal
   extends javax.ejb.EJBLocalObject
{
   /**
    * Routes a document
    * @return the list of routing rules PKs that matched
    * @throws XtentisException
    */
   public com.amalto.core.objects.routing.v2.ejb.RoutingRulePOJOPK[] route( com.amalto.core.ejb.ItemPOJOPK itemPOJOPK ) throws com.amalto.core.util.XtentisException;

   /**
    * Starts/restarts the router
    * @throws XtentisException
    */
   public void start(  ) throws com.amalto.core.util.XtentisException;

   /**
    * Stops the routing queue
    * @throws XtentisException
    */
   public void stop(  ) throws com.amalto.core.util.XtentisException;

   /**
    * Toggle suspend a routing queue
    * @throws XtentisException
    */
   public void suspend( boolean suspend ) throws com.amalto.core.util.XtentisException;

   /**
    * Toggle suspend a routing queue
    * @throws XtentisException
    */
   public int getStatus(  ) throws com.amalto.core.util.XtentisException;

}
