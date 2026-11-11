// ============
// stringutil.h
// ============

#ifndef _COMMON_STRINGUTIL_H_
#define _COMMON_STRINGUTIL_H_

#include "common.h"

class varString : public std::string
{
public:
	inline varString(const char* format, ...)
	{
		char temp[8192] = "";
		va_list args;
		va_start(args, format);
		vsnprintf(temp, sizeof(temp), format, args);
		va_end(args);

		std::string::operator=(temp);
	}

	inline operator const char*() const
	{
		return std::string::c_str();
	}
};

void strcpy_ext(char* dst, const char* src, const char* ext);
const char* stristr(const char* str, const char* substr, const char* nomatch = NULL);
char* stristr(char* str, const char* substr, char* nomatch = NULL);
const char* strskip(const char* str, const char* skip);
char* strskip(char* str, const char* skip);
const char* strsearch(const char* str, const char* search);
char* strsearch(char* str, const char* search);
bool if_strskip(const char*& str, const char* skip);

#endif // _COMMON_STRINGUTIL_H_
