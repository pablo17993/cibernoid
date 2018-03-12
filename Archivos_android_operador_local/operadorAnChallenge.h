#ifndef OPERADORANCHALLENGE_H
#define OPERADORANCHALLENGE_H

    unsigned char ** getChallengeProtectParams();
    unsigned char ** getChallengeUnProtectParams();
    unsigned char** executeParam();
    unsigned char* execute(unsigned char** parametrosXml);
    unsigned char** getParamNames();
    int getNParams();

#endif