/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.1
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */


using System;
using System.Runtime.InteropServices;

public class aiNode : IDisposable {
  private HandleRef swigCPtr;
  protected bool swigCMemOwn;

  internal aiNode(IntPtr cPtr, bool cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = new HandleRef(this, cPtr);
  }

  internal static HandleRef getCPtr(aiNode obj) {
    return (obj == null) ? new HandleRef(null, IntPtr.Zero) : obj.swigCPtr;
  }

  ~aiNode() {
    Dispose();
  }

  public virtual void Dispose() {
    lock(this) {
      if (swigCPtr.Handle != IntPtr.Zero) {
        if (swigCMemOwn) {
          swigCMemOwn = false;
          AssimpPINVOKE.delete_aiNode(swigCPtr);
        }
        swigCPtr = new HandleRef(null, IntPtr.Zero);
      }
      GC.SuppressFinalize(this);
    }
  }

  public aiNodeVector mChildren { get { return GetmChildren(); } }
  public UintVector mMeshes { get { return GetmMeshes(); } }

  public aiString mName {
    set {
      AssimpPINVOKE.aiNode_mName_set(swigCPtr, aiString.getCPtr(value));
    } 
    get {
      IntPtr cPtr = AssimpPINVOKE.aiNode_mName_get(swigCPtr);
      aiString ret = (cPtr == IntPtr.Zero) ? null : new aiString(cPtr, false);
      return ret;
    } 
  }

  public aiMatrix4x4 mTransformation {
    set {
      AssimpPINVOKE.aiNode_mTransformation_set(swigCPtr, aiMatrix4x4.getCPtr(value));
    } 
    get {
      IntPtr cPtr = AssimpPINVOKE.aiNode_mTransformation_get(swigCPtr);
      aiMatrix4x4 ret = (cPtr == IntPtr.Zero) ? null : new aiMatrix4x4(cPtr, false);
      return ret;
    } 
  }

  public aiNode mParent {
    set {
      AssimpPINVOKE.aiNode_mParent_set(swigCPtr, aiNode.getCPtr(value));
    } 
    get {
      IntPtr cPtr = AssimpPINVOKE.aiNode_mParent_get(swigCPtr);
      aiNode ret = (cPtr == IntPtr.Zero) ? null : new aiNode(cPtr, false);
      return ret;
    } 
  }

  public uint mNumChildren {
    set {
      AssimpPINVOKE.aiNode_mNumChildren_set(swigCPtr, value);
    } 
    get {
      uint ret = AssimpPINVOKE.aiNode_mNumChildren_get(swigCPtr);
      return ret;
    } 
  }

  public uint mNumMeshes {
    set {
      AssimpPINVOKE.aiNode_mNumMeshes_set(swigCPtr, value);
    } 
    get {
      uint ret = AssimpPINVOKE.aiNode_mNumMeshes_get(swigCPtr);
      return ret;
    } 
  }

  public aiNode() : this(AssimpPINVOKE.new_aiNode__SWIG_0(), true) {
  }

  public aiNode(string name) : this(AssimpPINVOKE.new_aiNode__SWIG_1(name), true) {
    if (AssimpPINVOKE.SWIGPendingException.Pending) throw AssimpPINVOKE.SWIGPendingException.Retrieve();
  }

  public aiNode FindNode(aiString name) {
    IntPtr cPtr = AssimpPINVOKE.aiNode_FindNode__SWIG_0(swigCPtr, aiString.getCPtr(name));
    aiNode ret = (cPtr == IntPtr.Zero) ? null : new aiNode(cPtr, false);
    if (AssimpPINVOKE.SWIGPendingException.Pending) throw AssimpPINVOKE.SWIGPendingException.Retrieve();
    return ret;
  }

  public aiNode FindNode(string name) {
    IntPtr cPtr = AssimpPINVOKE.aiNode_FindNode__SWIG_1(swigCPtr, name);
    aiNode ret = (cPtr == IntPtr.Zero) ? null : new aiNode(cPtr, false);
    return ret;
  }

  private aiNodeVector GetmChildren() {
    IntPtr cPtr = AssimpPINVOKE.aiNode_GetmChildren(swigCPtr);
    aiNodeVector ret = (cPtr == IntPtr.Zero) ? null : new aiNodeVector(cPtr, true);
    return ret;
  }

  private UintVector GetmMeshes() {
    IntPtr cPtr = AssimpPINVOKE.aiNode_GetmMeshes(swigCPtr);
    UintVector ret = (cPtr == IntPtr.Zero) ? null : new UintVector(cPtr, true);
    return ret;
  }

}
