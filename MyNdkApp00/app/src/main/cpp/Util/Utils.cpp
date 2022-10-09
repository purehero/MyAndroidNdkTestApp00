//
// Created by purehero on 2022-06-03.
//

#include "Utils.h"
#include <sys/mman.h>
#include <unistd.h>

const char * Utils::getMessage()
{
    LOGT();

    return "Hello from C++";
}

int change_page_permissions_of_address( void *addr ) {
    int page_size = getpagesize();

    unsigned long nAddr = ( unsigned long ) addr;
    nAddr -= nAddr % page_size;

    if( mprotect((void *) nAddr, page_size, PROT_READ | PROT_WRITE | PROT_EXEC) == -1 ) {
        return -1;
    }

    return 0;
}