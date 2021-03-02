// code found at:
//http://www.cellperformance.com/articles/2006/06/a_4x4_matrix_inverse_1.html


float cofactor_ij(mat4 mat, int col, int row)
{
	unsigned int sel0[] = { 1,0,0,0 };
	unsigned int sel1[] = { 2,2,1,1 };
	unsigned int sel2[] = { 3,3,3,2 };
	    
    // Let's first define the 3x3 matrix:
    const unsigned int col0 = sel0[col]; //1
    const unsigned int col1 = sel1[col]; //2
    const unsigned int col2 = sel2[col]; //3
    const unsigned int row0 = sel0[row]; //1
    const unsigned int row1 = sel1[row]; //2
    const unsigned int row2 = sel2[row]; //3
	
    
    // Computer the det of the 3x3 matrix:
    // 
    //   [ a b c ]
    //   [ d e f ] = aei - ahf + dhc - dbi + gbf - gec = (aei + dhc + gbf) - (ahf + dbi + gec)
    //   [ g h i ] 
    //
    
    const float	posPart1 = mat[col0][row0] * mat[col1][row1] * mat[col2][row2]; // aei
    const float	posPart2 = mat[col0][row1] * mat[col1][row2] * mat[col2][row0]; // dhc
    const float	posPart3 = mat[col0][row2] * mat[col1][row0] * mat[col2][row1]; // gbf
                                                                                                        
    const float	negPart1 = mat[col0][row0] * mat[col1][row2] * mat[col2][row1]; // ahf
    const float	negPart2 = mat[col0][row1] * mat[col1][row0] * mat[col2][row2]; // dbi
    const float	negPart3 = mat[col0][row2] * mat[col1][row1] * mat[col2][row0]; // gec	
    
    
    const float	posPart  = posPart1 + posPart2 + posPart3;
    const float	negPart  = negPart1 + negPart2 + negPart3;
    float	det3x3	  = posPart - negPart;
    
    if(mod(col+row,2) != 0)
    	det3x3 = -det3x3;
    
    return det3x3;
}

	
void cofactor(inout mat4 cof, mat4 m)
{
    for(int col = 0; col < 4; col++ )
    {
        for(int row = 0; row < 4; row++ )
        {
            cof[col][row] = cofactor_ij(m, col, row);
        } //for
    } //for
}


float determinant(mat4 m, mat4 cof)
{
    float det = 0.0;
    for (int col = 0; col < 4; col++ )
        det += m[col][0] * cof[col][0];
    
    return det;
} //determinant





mat4 multi(mat4 adj, float factor)
{
	mat4 inv;
    for(int col = 0 ; col < 4 ; col++ )
    {
        for(int row = 0; row < 4; row++ )
        {
            inv[col][row] = adj[col][row] * factor;
        } //for
    } //for
    
    return inv;
} //mul


mat4 calculateInverseMatrix(mat4 matrix)
{
    mat4 cof;
    mat4 adj;
    float oodet;
    
    // InvM = (1/det(M)) * Transpose(Cofactor(M))
    
    cofactor(cof, matrix);
   
    oodet = 1.0 / determinant(matrix, cof);
    adj = transpose(cof);
    
    return multi(adj, oodet);
}