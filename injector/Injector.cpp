// TestLib1.cpp : Defines the exported functions for the DLL application.
//

#include "stdafx.h"
#include "vcclr.h"
#include "Injector.h"

using namespace ManagedInjector;

HINSTANCE hinstDLL;	
static unsigned int WM_GOBABYGO = ::RegisterWindowMessage(L"Injector_GOBABYGO!");
static HHOOK _messageHookHandle;

struct handle_data {
	unsigned long process_id;
	HWND best_handle;
};

BOOL is_main_window(HWND handle)
{   
	return GetWindow(handle, GW_OWNER) == (HWND)0 && IsWindowVisible(handle);
}

BOOL CALLBACK enum_windows_callback(HWND handle, LPARAM lParam)
{
	handle_data& data = *(handle_data*)lParam;
	unsigned long process_id = 0;
	GetWindowThreadProcessId(handle, &process_id);
	if (data.process_id != process_id || !is_main_window(handle)) {
		return TRUE;
	}
	data.best_handle = handle;
	return FALSE;   
}

HWND find_main_window(unsigned long process_id)
{
	handle_data data;
	data.process_id = process_id;
	data.best_handle = 0;
	EnumWindows(enum_windows_callback, (LPARAM)&data);
	return data.best_handle;
}



LRESULT __stdcall MessageHookProc(int nCode, WPARAM wparam, LPARAM lparam)
{
	
	if (nCode == HC_ACTION)
	{
		
		CWPSTRUCT* msg = (CWPSTRUCT*)lparam;
		if (msg != NULL && msg->message == WM_GOBABYGO)
		{
			
			wchar_t* acmRemote = (wchar_t*)msg->wParam;

			System::String^ acmLocal = gcnew System::String(acmRemote);
			System::Diagnostics::Debug::WriteLine(System::String::Format("acmLocal = {0}", acmLocal));
			cli::array<System::String^>^ acmSplit = acmLocal->Split('$');

			System::Diagnostics::Debug::WriteLine(System::String::Format("About to load assembly {0}", acmSplit[0]));
			System::Reflection::Assembly^ assembly = System::Reflection::Assembly::LoadFile(acmSplit[0]);
			if (assembly != nullptr)
			{
				//MessageBox(NULL,TEXT("assembly"), TEXT("Dll says:"), MB_OK);

				System::Diagnostics::Debug::WriteLine(System::String::Format("About to load type {0}", acmSplit[1]));
				System::Type^ type = assembly->GetType(acmSplit[1]);
				if (type != nullptr)
				{
					//MessageBox(NULL,TEXT("type"), TEXT("Dll says:"), MB_OK);
					System::Diagnostics::Debug::WriteLine(System::String::Format("Just loaded the type {0}", acmSplit[1]));
					System::Reflection::MethodInfo^ methodInfo = type->GetMethod(acmSplit[2], System::Reflection::BindingFlags::Static | System::Reflection::BindingFlags::Public);
					if (methodInfo != nullptr)
					{
						//MessageBox(NULL,TEXT("method"), TEXT("Dll says:"), MB_OK);
						System::Diagnostics::Debug::WriteLine(System::String::Format("About to invoke {0} on type {1}", methodInfo->Name, acmSplit[1]));
						cli::array<System::String^>^ param = acmSplit[3]->Split(',');

						System::Object ^ returnValue = methodInfo->Invoke(nullptr, param);
						if (nullptr == returnValue)
							//MessageBox(NULL,TEXT("Hello World!"), TEXT("Dll says:"), MB_OK);
						returnValue = "NULL";
						System::Diagnostics::Debug::WriteLine(System::String::Format("Return value of {0} on type {1} is {2}", methodInfo->Name, acmSplit[1], returnValue));
					}
				}
			}
		}
		//	return CallNextHookEx(_messageHookHandle, nCode, wparam, lparam);
		return ::DefWindowProc(find_main_window(GetCurrentProcessId()),NULL, wparam, lparam);
		
	}
	
}


void Injector::Launch(System::Int32 processId, System::String^ assemblyPath, System::String^ className, System::String^ methodName, System::String^ param)
{
	HWND windowHandle = find_main_window(processId);

	//System::String^ assemblyPath = "TestServer.exe";
	//System::String^ className ="WpfApplication2.ClientApplication";
	//System::String^ methodName ="showMessage";

	System::String^ assemblyClassAndMethod = assemblyPath + "$" + className + "$" + methodName + "$" + param;

	pin_ptr<const wchar_t> acmLocal = PtrToStringChars(assemblyClassAndMethod);

	HINSTANCE hinstDLL;	

	if (::GetModuleHandleEx(GET_MODULE_HANDLE_EX_FLAG_FROM_ADDRESS, (LPCTSTR)&MessageHookProc, &hinstDLL))
	{
		
		DWORD processID = 0;
		DWORD threadID = ::GetWindowThreadProcessId((HWND)windowHandle, &processID);

		if (processID)
		{
			
			HANDLE hProcess = ::OpenProcess(PROCESS_ALL_ACCESS, FALSE, processID);
			
			if (hProcess)
			{
				
				int buffLen = (assemblyClassAndMethod->Length + 1) * sizeof(wchar_t);
				void* acmRemote = ::VirtualAllocEx(hProcess, NULL, buffLen, MEM_COMMIT, PAGE_READWRITE);

				if (acmRemote)
				{
					
					::WriteProcessMemory(hProcess, acmRemote, acmLocal, buffLen, NULL);
				
					_messageHookHandle = ::SetWindowsHookEx(WH_CALLWNDPROC, &MessageHookProc, hinstDLL, threadID);

					if (_messageHookHandle)
					{
						//::VirtualFreeEx(hProcess, acmRemote, 0, MEM_RELEASE);
						::SendMessage((HWND)windowHandle, WM_GOBABYGO, (WPARAM)acmRemote, 0);
						::UnhookWindowsHookEx(_messageHookHandle);
						_messageHookHandle = NULL;
					}

					::VirtualFreeEx(hProcess, acmRemote, 0, MEM_RELEASE);
				}

				BOOL res = ::CloseHandle(hProcess);
				
				
				hProcess = 0;
			}
		}
		::FreeLibrary(hinstDLL);
		hinstDLL = NULL;
	}
}



//
//bool WINAPI DllMain(HINSTANCE hInstDll, DWORD fdwReason, LPVOID lpvReserved)
//{
//	switch (fdwReason)
//	{
//	case DLL_PROCESS_ATTACH:
//		{
//			MessageBox(NULL, TEXT("Loaded"), TEXT("Dll says:"), MB_OK);
//			DWORD processID = 0;
//			DWORD threadID  = GetCurrentThreadId();
//			DWORD threadID = ::GetWindowThreadProcessId((HWND)windowHandle.ToPointer(), &processID);
//			HWND hWndMain = find_main_window(GetCurrentProcessId());
//			MessageBox(NULL, LPCTSTR(hWndMain) , TEXT("Dll says:"), MB_OK);
//			_messageHookHandle = ::SetWindowsHookEx(WH_CALLWNDPROC,&MessageHookProc, hInstDll,threadID);
//			::SendMessage(hWndMain, WM_GOBABYGO, NULL, 0);
//			::UnhookWindowsHookEx(_messageHookHandle);
//			
//			
//			break;
//		}
//	case DLL_PROCESS_DETACH:
//		break;
//	case DLL_THREAD_ATTACH:
//		break;
//	case DLL_THREAD_DETACH:
//		break;
//	}
//	return true;
//}
