

export interface Step {
    [parameter: string]: string[];
}

export interface Model {
    [step: string]: Step
}

export const model: Model = {
    assetMaster: { 
        model: ['LimaCVT1_PD30_TI70_5000', 'LimaCVT2_PD30_TI70_10000', 'LimaCVT3_PD30_TI70_50000', 'LimaCVT4_PD40_TI60_5000', 'LimaCVT5_PD40_TI60_10000', 'LimaCVT6_PD40_TI60_50000', 'LimaBlocks'] 
    },
    modelProp: { 
        schema: ["SARA_v1.0"] 
    },
};