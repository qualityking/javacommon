// (c) Copyright Cory Plotts.
// This source is subject to the Microsoft Public License (Ms-PL).
// Please see http://go.microsoft.com/fwlink/?LinkID=131993 for details.
// All other rights reserved.

#pragma once

__declspec(dllexport)

LRESULT __stdcall MessageHookProc(int nCode, WPARAM wparam, LPARAM lparam);

using namespace System;

namespace ManagedInjector
{
	public ref class Injector : System::Object
	{
		public:

		static void Launch(System::Int32 processId, System::String^ assemblyPath, System::String^ className, System::String^ methodName, System::String^ param);

	};
}
