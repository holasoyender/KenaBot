// ========
// common.h
// ========

#ifndef _COMMON_COMMON_H_
#define _COMMON_COMMON_H_


#define RSG_CPU_X64 1

#define _CRT_SECURE_NO_WARNINGS
#include <windows.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <string.h>
#include <assert.h>
#include <math.h>
#include <float.h>

#include <string>
#include <vector>
#include <map>
#include <algorithm>

#ifdef LoadImage
#undef LoadImage // what?
#endif

typedef unsigned char          u8;
typedef char                   s8;
typedef unsigned short         u16;
typedef short                  s16;
typedef unsigned int           u32;
typedef int                    s32;
typedef unsigned long long int u64;
typedef long long int          s64;

template <typename T> __forceinline T Min(T a, T b) { return a <= b ? a : b; }
template <typename T> __forceinline T Max(T a, T b) { return a >= b ? a : b; }
template <typename T> __forceinline T Abs(T a) { return a >= T(0) ? a : -a; }
template <typename T> __forceinline T Clamp(T value, T valueMin, T valueMax) { return Min<T>(Max<T>(value, valueMin), valueMax); }

template <typename T> __forceinline T Min(T a, T b, T c) { return Min<T>(a, Min<T>(b, c)); }
template <typename T> __forceinline T Max(T a, T b, T c) { return Max<T>(a, Max<T>(b, c)); }

template <typename T> __forceinline T Min(T a, T b, T c, T d) { return Min<T>(a, Min<T>(b, c, d)); }
template <typename T> __forceinline T Max(T a, T b, T c, T d) { return Max<T>(a, Max<T>(b, c, d)); }

#define PI 3.1415926535897932384626433832795f

#define NELEM(x) (sizeof(x)/sizeof((x)[0]))
#define BIT(x) (1<<(x))
#define BIT64(x) (1ULL<<(x))
#define CLEAR_BIT(mask,index) mask[(index)/8] &= ~BIT((index)%8)
#define SET_BIT(mask,index) mask[(index)/8] |= BIT((index)%8)
#define TEST_BIT(mask,index) (mask[(index)/8] & BIT((index)%8))

#define INDEX_NONE (-1)

#define MacroJoin( X, Y ) MacroDoJoin(X, Y)
#define MacroDoJoin( X, Y ) MacroDoJoin2(X, Y)
#define MacroDoJoin2( X, Y ) X##Y
#define CompileTimeAssert(e) typedef char MacroJoin(MacroJoin(__COMPILETIME_ASSERT, __LINE__), __)[(e)?1:-1]

#define STRING(x) #x
#define __COMMENT(x)

#define Assert assert
#define rage_new new

#endif // _COMMON_COMMON_H_
